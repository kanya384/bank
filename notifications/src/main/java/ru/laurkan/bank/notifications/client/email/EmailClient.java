package ru.laurkan.bank.notifications.client.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.exception.EmailSendException;
import ru.laurkan.bank.notifications.model.EmailNotification;

import java.util.Properties;

@Component
public class EmailClient {
    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private Long port;

    private Session session;

    @PostConstruct
    private void initSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl.trust", host);

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public Mono<EmailNotification> sendMessage(EmailNotification emailNotification) {
        return generateMessage(emailNotification)
                .doOnNext(message -> {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        throw new EmailSendException(e.getMessage());
                    }
                }).map(m -> emailNotification);
    }

    private Mono<Message> generateMessage(EmailNotification emailNotification) {
        return Mono.just((Message) new MimeMessage(session))
                .map(message -> {
                    try {
                        message.setFrom(new InternetAddress(username));

                        message.setRecipients(
                                Message.RecipientType.TO, InternetAddress.parse(emailNotification.getRecipient()));
                        message.setSubject(emailNotification.getSubject());

                        MimeBodyPart mimeBodyPart = new MimeBodyPart();
                        mimeBodyPart.setContent(emailNotification.getMessage(), "text/html; charset=utf-8");

                        Multipart multipart = new MimeMultipart();
                        multipart.addBodyPart(mimeBodyPart);

                        message.setContent(multipart);
                        return message;
                    } catch (MessagingException e) {
                        throw new EmailSendException(e.getMessage());
                    }
                });
    }
}
