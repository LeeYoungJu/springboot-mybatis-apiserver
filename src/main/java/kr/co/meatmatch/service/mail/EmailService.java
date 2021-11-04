package kr.co.meatmatch.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    private final String FROM_ADDR = "kamex1@gbnet.kr";

    public void sendToAdmin(String template, String subject, HashMap<String, Object> data) throws MessagingException, IOException {
        String[] admins = {"dmlqhqhqh@naver.com"};
        for (String admin : admins) {
            this.sendMail(template, subject, admin, data);
        }
    }

    public void sendMail(String template, String subject, String to, HashMap<String, Object> data) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(subject);
        helper.setFrom(FROM_ADDR);
        helper.setTo(to);
        Context context = new Context();
        for(String key : data.keySet()) {
            context.setVariable(key, data.get(key));
        }
        String html = templateEngine.process(template, context);
        helper.setText(html, true);

        emailSender.send(message);
    }
}
