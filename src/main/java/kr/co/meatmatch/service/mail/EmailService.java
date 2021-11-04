package kr.co.meatmatch.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    private final String FROM_ADDR = "kamex1@gbnet.kr";

    public void sendTextEmail(String toAddr, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDR);
        message.setTo(toAddr);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
