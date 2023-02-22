package com.example.emailsender.service;

import com.example.emailsender.entity.Email;
import com.example.emailsender.services.EmailSenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Test
    public void sendHtmlMessageTest() {
        Email email = new Email();
        email.setTo("thienvykg123@gmail.com");
        email.setFrom("thienvysg@gmail.com");
        email.setSubject("test test test");
        email.setTemplate("verification.html");
        Map<String, Object> properties = new HashMap<>();
//        properties.put();
        email.setProperties(properties);

        Assertions.assertDoesNotThrow(() -> emailSenderService.sendHtmlMessage(email));
    }
}