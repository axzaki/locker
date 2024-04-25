package com.test.locker.helper;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailHelper {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void sendEmailAsync(String to, String subject, String body) {
        executorService.submit(() -> {
            try {
                sendEmail(to, subject, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void sendEmail(String to, String subject, String body) throws Exception {
        try {
            Properties props = new Properties();
//            FileInputStream input = new FileInputStream("email.properties");
            FileInputStream input = new FileInputStream("D:\\workspace\\locker\\src\\main\\resources\\email.properties");
            props.load(input);

            String smtpUsername = props.getProperty("smtp.username");
            String smtpPassword = props.getProperty("smtp.password");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        }catch (Exception e){
            throw new Exception("Failed to send email: " + e.getMessage());
        }

    }
}

