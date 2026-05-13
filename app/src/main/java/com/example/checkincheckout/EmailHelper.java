package com.example.checkincheckout;

import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {

    static final String FROM_EMAIL = "anisatya2019@gmail.com";
    static final String APP_PASSWORD = "nytw iacd tfch ovls";

    // Async — for use from UI / Activities
    public static void sendEmail(String toEmail, String subject, String message) {
        new Thread(() -> sendEmailBlocking(toEmail, subject, message)).start();
    }

    // Blocking — for use from BroadcastReceiver after goAsync()
    public static void sendEmailBlocking(String toEmail, String subject, String message) {
        try {
            Log.d("EMAIL_DEBUG", "Sending to: " + toEmail);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                        }
                    });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);
            Log.d("EMAIL", "SUCCESS → " + toEmail);

        } catch (Exception e) {
            Log.e("EMAIL", "FAILED → " + e.getMessage());
            e.printStackTrace();
        }
    }
}