package com.mysuperproject.atelier.infrastructure.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpEmailSender implements EmailSender {

  // Для реальної розсилки вкажіть ваш email та App Password (пароль додатку)
  private final String username = "slavaborsak67@gmail.com";
  private final String password = "habq qyhn msci ulyq";

  @Override
  public void sendEmail(String to, String subject, String body) {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true"); // TLS

    Session session = Session.getInstance(prop, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setContent(body, "text/html; charset=utf-8");

      Transport.send(message);

      System.out.println("Лист успішно відправлено на " + to);

    } catch (MessagingException e) {
      e.printStackTrace();
      System.err.println("Помилка відправлення листа: " + e.getMessage());
    }
  }
}
