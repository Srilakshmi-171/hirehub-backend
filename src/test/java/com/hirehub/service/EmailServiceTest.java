
package com.hirehub.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.hirehub.util.EmailTemplateUtil;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Test
    void shouldSendApplicationConfirmationEmail() {

        String htmlBody =
                EmailTemplateUtil.applicationConfirmation(
                        "HireHub Test User",
                        "Java Backend Developer",
                        "HireHub Technologies"
                );

        emailService.sendHtmlEmail(
                emailAddress,
                "HireHub - Email Delivery Test",
                htmlBody
        );
    }
}
