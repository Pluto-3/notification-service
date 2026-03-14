package com.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.from-name}")
    private String fromName;

    public void sendWithTemplate(String to, String subject, String templateName, Map<String, Object> data)
            throws MessagingException, java.io.UnsupportedEncodingException {
        Context context = new Context();
        if (data != null) {
            data.forEach(context::setVariable);
        }
        String htmlContent = templateEngine.process(templateName, context);
        sendHtml(to, subject, htmlContent);
    }

    public void sendPlain(String to, String subject, String message) throws MessagingException, java.io.UnsupportedEncodingException {
        sendHtml(to, subject, message);
    }

    private void sendHtml(String to, String subject, String htmlContent) throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
        log.info("Email sent to {} | subject: {}", to, subject);
    }
}