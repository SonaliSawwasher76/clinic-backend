package com.clinic.config;   // or com.clinic.service.payment

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayClientFactory {

    private final RazorpayProps props;

    public RazorpayClient create() throws RazorpayException {
        return new RazorpayClient(props.getKeyId(), props.getKeySecret());
    }
}
