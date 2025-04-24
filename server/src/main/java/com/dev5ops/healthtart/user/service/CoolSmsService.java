package com.dev5ops.healthtart.user.service;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CoolSmsService {
    private final DefaultMessageService messageService;

    public CoolSmsService (DefaultMessageService messageService) {
        this.messageService = messageService;
    }

    // 랜덤한 4자리 인증번호 생성
    public String generateRandomNumber() {
        Random random = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            numStr.append(random.nextInt(10));
        }
        return numStr.toString();
    }

    // SMS 전송
    public void sendSms(String to, String text) {
        Message message = new Message();
        message.setFrom("01022089727");
        message.setTo(to);
        message.setText(text);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
