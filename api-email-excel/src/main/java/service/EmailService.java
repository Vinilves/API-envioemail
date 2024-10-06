package service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

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
    public void sendEmails(List<String> recipients, String subject, String message) throws MessagingException {
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

     // Loop para enviar e-mails somente para e-mails válidos
        for (String recipient : recipients) {
            recipient = recipient.trim(); // Remove espaços em branco
            if (!recipient.isEmpty() && isValidEmail(recipient)) { // Verifica se o e-mail é válido
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(username));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                msg.setSubject(subject);
                msg.setText(message);

                Transport.send(msg); // Envia o e-mail
            } else {
                System.out.println("E-mail inválido: " + recipient); // Opcional: logar e-mails inválidos
            }
        }    
    }
}

