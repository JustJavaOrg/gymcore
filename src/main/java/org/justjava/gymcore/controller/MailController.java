package org.justjava.gymcore.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.justjava.gymcore.model.MailModel;
import org.justjava.gymcore.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/sendMail/{to}")
    public ResponseEntity<String> sendMail( @PathVariable("to")
            @Email(message = "Invalid email address format")
            String to,
            @Valid @RequestBody MailModel mailModel) {
        try {
            mailService.sendMail(to, mailModel.subject(), mailModel.body());
            return ResponseEntity.ok()
                    .body("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

}
