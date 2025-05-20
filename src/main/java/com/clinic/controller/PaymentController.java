package com.clinic.controller;

import com.clinic.service.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /* ─────────────────────────────────────────────
       1. CREATE ORDER
       ------------------------------------------------------------------
       Call example:
       GET /api/payments/order?visitRecordId=3
       ───────────────────────────────────────────── */
    @GetMapping("/order")
    public ResponseEntity<?> createOrder(@RequestParam Long visitRecordId) {
        try {
            Map<String, Object> response = paymentService.createOrder(visitRecordId);
            return ResponseEntity.ok(response);  // Spring will convert Map to JSON automatically
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Order creation failed: " + e.getMessage());
        }
    }


    /* ─────────────────────────────────────────────
       2. VERIFY PAYMENT SIGNATURE
       ------------------------------------------------------------------
       Razorpay will POST a JSON payload containing:
       {
         "razorpay_order_id": "...",
         "razorpay_payment_id": "...",
         "razorpay_signature": "..."
       }
       ───────────────────────────────────────────── */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPaymentSignature(@RequestBody String payloadStr) {
        try {
            JSONObject payload = new JSONObject(payloadStr);
            boolean valid = paymentService.verifyPaymentSignature(payload);

            if (valid) {
                return ResponseEntity.ok("Payment signature verified");
            }
            return ResponseEntity.badRequest().body("Invalid signature");

        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body("Verification failed: " + e.getMessage());
        }
    }
}
