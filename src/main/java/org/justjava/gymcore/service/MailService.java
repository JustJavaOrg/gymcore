package org.justjava.gymcore.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.justjava.gymcore.config.smtp.GoogleSmtpConfig;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final GoogleSmtpConfig googleSmtpConfig;

    // Host, Host e-mail, password and port will be provided under yaml file. If you want to get configurations from yaml file use smtpConfigurationService.
    public void sendMail(String to, String subject, String body) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", googleSmtpConfig.getHost());
        props.put("mail.smtp.auth", googleSmtpConfig.getAuth());
        props.put("mail.smtp.port", googleSmtpConfig.getPort());
        props.put("mail.smtp.starttls.enable", googleSmtpConfig.getStarttls());


        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(googleSmtpConfig.getUsername(), googleSmtpConfig.getPassword());
            }
        };

        Session session = Session.getInstance(props, authenticator);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(googleSmtpConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            log.info("Email sent successfully to: {}", to);
    }
}
