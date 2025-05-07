package ru.laurkan.bank.notifications.client.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.configuration.EmailClientConfiguration;
import ru.laurkan.bank.notifications.exception.EmailSendException;
import ru.laurkan.bank.notifications.model.EmailNotification;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class EmailClient {
    private final EmailClientConfiguration emailClientConfiguration;
    private Session session;

    @PostConstruct
    private void initSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", emailClientConfiguration.getHost());
        prop.put("mail.smtp.port", emailClientConfiguration.getPort());
        prop.put("mail.smtp.ssl.trust", emailClientConfiguration.getHost());

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailClientConfiguration.getUsername(), emailClientConfiguration.getPassword());
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
                        message.setFrom(new InternetAddress(emailClientConfiguration.getUsername()));

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
