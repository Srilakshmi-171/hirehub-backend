
package com.hirehub.service;

public interface EmailService {

    void sendHtmlEmail(
            String to,
            String subject,
            String htmlBody
    );
}
