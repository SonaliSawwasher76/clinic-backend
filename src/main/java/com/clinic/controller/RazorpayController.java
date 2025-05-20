package com.clinic.controller;
import com.razorpay.Order;
import com.clinic.service.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/razorpay")
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @GetMapping("/create-order")
    public ResponseEntity<String> createOrder() throws Exception {
        Order order = razorpayService.createOrder(10000);
        return ResponseEntity.ok(order.toString());
    }
}