package com.korenko.CBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordRecoveryEmail(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("korenko_2025@mail.ru");
        message.setTo(toEmail);
        message.setSubject("Восстановление пароля");
        message.setText("Здравствуйте!\n\n"
                + "Новый пароль для доступа к аккаунту: "
                + password);

        mailSender.send(message);
    }

    public void sendCancelRequest(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("korenko_2025@mail.ru");
        message.setTo(toEmail);
        message.setSubject("Отказ в восстановлении пароля");
        message.setText("Здравствуйте!\n\n"
                + "Ваш запрос на восстановление пароля был отклонен");

        mailSender.send(message);
    }
}
