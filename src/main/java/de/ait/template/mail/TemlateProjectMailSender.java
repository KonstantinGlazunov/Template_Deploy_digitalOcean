package de.ait.template.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * 24.10.2023
 * TemplateProject
 *
 * @author Konstantin Glazunov (AIT TR)
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class TemlateProjectMailSender {

    private final JavaMailSender javaMailSender;

    @Async
    public void send(String email, String subject, String text) {
        log.info("Current thread for email sending " + Thread.currentThread().getName());

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom("TemplateProjectAdmin");
        } catch (MessagingException e) {
            throw new IllegalStateException(e);
        }

        javaMailSender.send(message);
    }

}
