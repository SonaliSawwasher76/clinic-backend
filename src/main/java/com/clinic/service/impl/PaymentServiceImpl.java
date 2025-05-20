package com.clinic.service.impl;

import com.clinic.entity.Appointment.VisitRecord;
import com.clinic.entity.billing.PaymentHistory;
import com.clinic.entity.billing.Invoice;
import com.clinic.repository.VisitRecordRepository;
import com.clinic.service.services.PaymentService;
import com.clinic.service.services.VisitRecordService;
import com.clinic.repository.InvoiceRepository;
import com.clinic.repository.PaymentHistoryRepository;
import com.razorpay.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;
    private final VisitRecordService visitRecordService;
    private final InvoiceRepository  invoiceRepository;
    private final PaymentHistoryRepository  paymentHistoryRepository;
    private final VisitRecordRepository visitRecordRepository;

    /* ───────────────────────────────────────────── */
    @PostConstruct
    public void init() throws RazorpayException {
        razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    /* ------------------------------------------------------------------
       CREATE RAZORPAY ORDER  – expects visitRecordId, not raw amount
       ------------------------------------------------------------------ */
    @Override
    public Map<String, Object> createOrder(Long visitRecordId) throws RazorpayException {
        // Your logic to get amount from visitRecordId, etc.

        VisitRecord visitRecord = visitRecordRepository.findById(visitRecordId)
                .orElseThrow(() -> new IllegalArgumentException("VisitRecord not found"));


        Invoice invoice = invoiceRepository.findByVisitRecord_VisitRecordId(visitRecordId)
                .orElseGet(() -> {
                    String clinicName = visitRecord.getAppointment()
                            .getDoctor()
                            .getUser()
                            .getWorkspace()
                            .getName();

                    Invoice newInvoice = Invoice.builder()
                            .visitRecord(visitRecord)
                            .clinicName(clinicName)
                            .paymentStatus(Invoice.PaymentStatus.PENDING)
                            .build();

                    return invoiceRepository.save(newInvoice);
                });

        int amount = visitRecordService.getAmountByVisitRecordId(visitRecordId);
         // or fetch dynamically

        JSONObject options = new JSONObject();
        options.put("amount", amount);
        options.put("currency", "INR");
        options.put("payment_capture", 1);
        options.put("receipt", "visit-" + visitRecordId);
       // options.put("Clinic name",);

        Order order = razorpayClient.orders.create(options);
        JSONObject orderJson = order.toJson();

        Map<String, Object> response = new HashMap<>();
        response.put("order", orderJson.toMap());   // Convert JSON to Map for serialization
        response.put("keyId", keyId);
        response.put("clinicName", invoice.getClinicName());

        return response;
    }

    /* ------------------------------------------------------------------
       VERIFY SIGNATURE  – same as before
       ------------------------------------------------------------------ */

    @Override
    public boolean verifyPaymentSignature(JSONObject payload) throws RazorpayException {

        /* ── 1.  Verify that the signature really came from Razorpay ───────── */
        JSONObject opts = new JSONObject()
                .put("razorpay_order_id",  payload.getString("razorpay_order_id"))
                .put("razorpay_payment_id", payload.getString("razorpay_payment_id"))
                .put("razorpay_signature",  payload.getString("razorpay_signature"));

        Utils.verifyPaymentSignature(opts, keySecret);   // ← throws if tampered

        /* ── 2.  Fetch the final order object so we can store details ───────── */
        Order order   = razorpayClient.orders
                .fetch(payload.getString("razorpay_order_id"));

        /* amount_paid & currency can be NULL in test mode – guard the cast   */
        long   amountPaid = 0;
        Object amtObj     = order.get("amount_paid");         // may be JSONObject.NULL
        if (amtObj instanceof Number num) {
            amountPaid = num.longValue();                     // paise
        }

        String currency  = "INR";
        Object curObj    = order.get("currency");
        if (curObj instanceof String s) {
            currency = s;
        }

        /* ── 3.  Locate the related invoice (receipt holds “visit‑{id}”) ──── */
        String receipt        = order.get("receipt");   // e.g. visit‑3
        if (receipt == null || !receipt.startsWith("visit-")) {
            // either skip linking or log & fail ‑‑ your call
            throw new IllegalStateException(
                    "Cannot determine visitRecordId – receipt missing or invalid: " + receipt);
        }
        Long   visitRecordId  = Long.valueOf(receipt.split("-")[1]);

        Invoice invoice = invoiceRepository
                .findByVisitRecord_VisitRecordId(visitRecordId)
                .orElseThrow(() ->
                        new IllegalStateException("No invoice for visit " + visitRecordId));

        long amountPaidInRupees = amountPaid / 100;
        /* ── 4.  Persist payment record ────────────────────────────────────── */
        PaymentHistory history = PaymentHistory.builder()
                .invoice(invoice)
                .razorpayOrderId(   payload.getString("razorpay_order_id"))
                .razorpayPaymentId( payload.getString("razorpay_payment_id"))
                .razorpaySignature( payload.getString("razorpay_signature"))
                .amount(   amountPaidInRupees)          // paise stored as long
                .currency( currency )
                .status(   PaymentHistory.Status.SUCCESS)
                .paidAt(   LocalDateTime.now())
                .build();

        paymentHistoryRepository.save(history);

        /* ── 5.  Mark an invoice as PAID ── */
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
        invoiceRepository.save(invoice);

        return true;      // everything went well
    }

}
