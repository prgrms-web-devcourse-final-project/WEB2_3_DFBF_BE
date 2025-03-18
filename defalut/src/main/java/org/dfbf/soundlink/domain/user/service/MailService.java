package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    //인증코드 생성
    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(2);
            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 1 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }
    //인증 메일 생성
    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("SoundLink 이메일 인증");

        String body = "<div style='font-size: 14px; font-family: Gulim,굴림,sans-serif;'>"
                + "<div style='margin: 0 auto; padding: 0; font-family: 맑은고딕, Malgun Gothic, dotum, gulim, sans-serif; letter-spacing: -0.6px; width: 100%;'>"
                + "<table cellpadding='0' cellspacing='0' width='600' style='margin: 0 auto; width: 600px; padding: 0; font-family: 맑은고딕, Malgun Gothic, dotum, gulim, sans-serif; letter-spacing: -0.6px; line-height: 1.5; background-color: #fff;'>"
                + "<tbody>"
                + "<tr>"
                + "<td style='border-bottom: 1px solid #242424; margin: 0; padding: 24px 40px; letter-spacing: -0.6px;'>"
                + "<table cellpadding='0' cellspacing='0' style='margin: 0; padding: 0; width: 100%; line-height: 1.5;'>"
                + "<tbody>"
                + "<tr>"
                + "<td style='padding-bottom: 10px;'>"
                + "<img src='https://github.com/user-attachments/assets/4187f56f-9500-4e90-a76d-6305a8344dc7' width='80' alt='사운드링크' style='margin: 0; padding: 0;' loading='lazy'/>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>"
                + "<b style='font-size: 24px; line-height: 1.3; letter-spacing: -0.6px;'>"
                + "사운드링크<br/>"
                + "인증번호 안내"
                + "</b>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='margin: 0; padding: 40px; letter-spacing: -0.6px;'>"
                + "<table cellpadding='0' cellspacing='0' style='width: 100%; line-height: 28px;'>"
                + "<tbody>"
                + "<tr>"
                + "<td style='padding-bottom: 12px; color: #555555; font-size: 16px;'>"
                + "안녕하세요.<br/>"
                + "사운드링크입니다.<br/>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='line-height: 1.7; color: #555555; font-size: 16px;'>"
                + "회원님께서 요청하신 이메일 인증 코드를 발송해드립니다.<br/>"
                + "아래 인증번호를 입력하여 이메일 인증을 완료해주세요."
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='margin: 0; padding: 40px; background: #FBEAFF; letter-spacing: -0.6px; text-align: center;'>"
                + "<table cellpadding='0' cellspacing='0' style='width: 100%; line-height: 1.5; background: #FBEAFF;'>"
                + "<tbody>"
                + "<tr>"
                + "<td align='center' style='color: #555; padding-bottom: 16px;'>"
                + "<h3 style='font-weight: bold; font-size: 16px; line-height: 28px; color: #242424; margin: 0; padding-bottom: 12px;'>"
                + "인증번호"
                + "</h3>"
                + "<div style='font-weight: bold; font-size: 32px; text-align: center; color: #242424; padding: 20px; background: #FFFFFF; border-radius: 16px; display: inline-block;'>"
                + authCode
                + "</div>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</div>"
                + "</div>";

        message.setText(body, "UTF-8", "html");

        return message;
    }

    //메일 발송
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String authCode = createCode();
        MimeMessage message = createMail(sendEmail, authCode);

        try {
            javaMailSender.send(message);
            return authCode;
        } catch (MailException e) {
            return null;
        }
    }
}

