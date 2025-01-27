package com.example.travelshooting.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendMail(String to, String subject, String text) {
        try {
            // 메일 양식으로 html 파일을 적용하기 위해 MimeMessage 객체를 활용
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 파일 첨부를 안 하기 때문에 multipart = false
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // html 여부 true

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.");
        }
    }

    public String processTemplate(String templatePath, Map<String, Object> contents) {
        Context context = new Context();
        context.setVariables(contents);

        return templateEngine.process(templatePath, context);
    }
}
