package com.example.checkincheckout;

import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;

import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {

    private static final String EMAIL = "anisatya2019@gmail.com";
    private static final String PASSWORD = "xotw aghh fgsn hgjz";
    public  static void sendMail(String toEmail, String subject, String message) {

        new Thread(() -> {
            try {

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                javax.mail.Session session = javax.mail.Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            @Override
                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                return new javax.mail.PasswordAuthentication(EMAIL, PASSWORD);
                            }
                        });

                javax.mail.Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(EMAIL));
                msg.setRecipients(javax.mail.Message.RecipientType.TO,
                        InternetAddress.parse(toEmail));
                msg.setSubject(subject);
                msg.setText(message);

                Transport.send(msg);

                Log.d("EMAIL", "SENT → " + toEmail);

            } catch (Exception e) {
                Log.e("EMAIL", "FAILED → " + e.getMessage());
            }
        }).start();
    }

}