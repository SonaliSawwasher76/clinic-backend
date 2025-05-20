package com.clinic.service.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private RazorpayClient client;

    public RazorpayService(@Value("${razorpay.key-id}") String keyId,
                           @Value("${razorpay.key-secret}") String keySecret) throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    public Order createOrder(int amountInPaise) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise); // amount in paise
        options.put("currency", "INR");
        options.put("payment_capture", 1);

        return client.orders.create(options);
    }
}
