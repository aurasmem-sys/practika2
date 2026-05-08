package com.mysuperproject.atelier.infrastructure.mail;

public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: ");
        System.out.println(body);
        System.out.println("==================");
    }
}
