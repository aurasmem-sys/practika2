package com.mysuperproject.atelier.infrastructure.mail;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}
