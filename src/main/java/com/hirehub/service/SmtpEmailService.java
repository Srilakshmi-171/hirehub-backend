
package com.hirehub.service;

import java.nio.charset.StandardCharsets;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendHtmlEmail(
            String to,
            String subject,
            String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            logger.info("Email sent successfully. Subject: {}", subject);

        } catch (MessagingException | MailException ex) {

            logger.error(
                    "Email delivery failed. Exception type: {}",
                    ex.getClass().getSimpleName(),
                    ex
            );

            throw new IllegalStateException(
                    "Failed to send email",
                    ex
            );
        }
    }
}
