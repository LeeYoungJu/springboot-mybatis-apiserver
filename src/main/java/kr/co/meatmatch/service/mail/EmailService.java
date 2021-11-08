package kr.co.meatmatch.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${constants.mail-from}")
    String FROM_ADDR;

    @Value("${constants.mail-admins}")
    String[] ADMINS;

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
        String html = templateEngine.process("mail/"+template, context);
        helper.setText(html, true);

        emailSender.send(message);
    }

    public void sendToAdmin(String template, String subject, HashMap<String, Object> data) throws MessagingException, IOException {
        this.sendMails(template, subject, ADMINS, data);
    }

    public void sendMails(String template, String subject, String[] to, HashMap<String, Object> data) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(subject);
        helper.setFrom(FROM_ADDR);
        helper.setTo(to);
        Context context = new Context();
        for(String key : data.keySet()) {
            context.setVariable(key, data.get(key));
        }
        String html = templateEngine.process("mail/"+template, context);
        helper.setText(html, true);

        emailSender.send(message);

        for(String admin : ADMINS) {
            System.out.println(admin);
        }
    }
}
