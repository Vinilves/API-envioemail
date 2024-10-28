package com.api.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

@Service
public class EmailService {

    // Variáveis de configuração para login de e-mail (injetadas a partir do application.properties)
    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    // Método auxiliar para validar e-mails (reutilizado)
    public boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    // Método para enviar e-mails
    public void sendEmails(List<String> recipients, String assunto, String descricao) throws MessagingException {
        // Configurações do servidor de e-mail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Host SMTP
        props.put("mail.smtp.port", "587"); // Porta SMTP

        // Autenticação para o servidor de e-mail
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Set<String> uniqueRecipients = new HashSet<>(recipients);

        // Loop para enviar e-mails somente para e-mails válidos
        for (String recipient : uniqueRecipients) {
            recipient = recipient.trim();
            if (!recipient.isEmpty() && isValidEmail(recipient)) {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(username));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                msg.setSubject(assunto);
                msg.setText(descricao);
                Transport.send(msg);
            } else {
                System.out.println("E-mail inválido: " + recipient);
            }
        }
    }

    // Método para carregar e-mails de um arquivo
    public List<String> getEmailsFromFile(String filePath) throws IOException {
        List<String> emailList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String email = line.trim();
                if (isValidEmail(email)) {
                    emailList.add(email);
                } else {
                    System.out.println("E-mail inválido no arquivo: " + email);
                }
            }
        }
        return emailList;
    }
}
