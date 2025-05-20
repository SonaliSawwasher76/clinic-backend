package com.clinic.service.services;

import org.json.JSONObject;

import java.util.Map;

public interface PaymentService {


    // PaymentService.java
    Map<String, Object> createOrder(Long visitRecordId) throws Exception;


    boolean verifyPaymentSignature(JSONObject payload) throws Exception;
}
