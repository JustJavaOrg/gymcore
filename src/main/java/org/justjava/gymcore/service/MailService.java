package org.justjava.gymcore.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.justjava.gymcore.config.smtp.GoogleSmtpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    @Autowired
    private GoogleSmtpConfig googleSmtpConfig;

    // Host, Host e-mail, password and port will be provided under yaml file. If you want to get configurations from yaml file use smtpConfigurationService.
    public ResponseEntity<HttpStatus> sendMail(String to, String subject, String body) {
        Logger logger = Logger.getLogger(MailService.class.getName());


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

        Session session = Session.getDefaultInstance(props, authenticator);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(googleSmtpConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
