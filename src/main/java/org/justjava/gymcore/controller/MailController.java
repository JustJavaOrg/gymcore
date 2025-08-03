package org.justjava.gymcore.controller;

import org.justjava.gymcore.model.MailModel;
import org.justjava.gymcore.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/sendMail/{to}")
    public ResponseEntity<HttpStatus> sendMail(@PathVariable("to") String to, @RequestBody MailModel mailModel) {
        return mailService.sendMail(to, mailModel.subject(), mailModel.body());
    }

}
