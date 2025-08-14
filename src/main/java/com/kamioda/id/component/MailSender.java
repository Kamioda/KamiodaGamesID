package com.kamioda.id.component;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class MailSender {

    private final Session session;
    private final String fromAddressInternal;
    private final String fromNameInternal;

    public MailSender(
            @Value("${mail.smtp.host}") String smtpHost,
            @Value("${mail.smtp.port}") int smtpPort,
            @Value("${mail.user.name}") String username,
            @Value("${mail.user.password}") String password,
            @Value("${mail.from.address}") String fromAddress,
            @Value("${mail.from.name}") String fromName
    ) {
        this.fromAddressInternal = fromAddress;
        this.fromNameInternal = fromName;

        Properties serverInfo = new Properties();
        serverInfo.put("mail.smtp.host", smtpHost);
        serverInfo.put("mail.smtp.port", Integer.toString(smtpPort));
        serverInfo.put("mail.smtp.auth", "true");
        // 必要なら STARTTLS/SSL を有効化:
        // serverInfo.put("mail.smtp.starttls.enable", "true");
        // serverInfo.put("mail.smtp.ssl.enable", "true");

        this.session = Session.getInstance(serverInfo, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void send(String to, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(
                    fromAddressInternal,
                    fromNameInternal,
                    StandardCharsets.UTF_8.name()
            ));
            msg.setRecipients(Message.RecipientType.TO, to); // 複数指定は "a@x,y@z" 形式でOK
            msg.setSubject(subject, StandardCharsets.UTF_8.name());
            msg.setText(body, StandardCharsets.UTF_8.name());
            Transport.send(msg);
        } catch (Exception e) {
            // 必要に応じてロギングや再スロー
            throw new RuntimeException("Failed to send mail", e);
        }
    }
}
