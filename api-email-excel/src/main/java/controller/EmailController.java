package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import service.EmailService;
import service.ExcelService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private ExcelService excelService; // Injeta o serviço de leitura de Excel

    @Autowired
    private EmailService emailService; // Injeta o serviço de envio de e-mail

    // Endpoint para enviar e-mails
    @PostMapping("/enviar")
    public String sendEmailsFromExcel(@RequestParam("file") MultipartFile file, @RequestParam("subject") String subject, @RequestParam("message") String message) throws IOException, MessagingException {
        File tempFile = null;

        try {
            // Cria um arquivo temporário para armazenar a planilha
            tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile); // Transfere o conteúdo do MultipartFile para o arquivo temporário

            // Extrai os e-mails da planilha e envia as mensagens
            List<String> emails = excelService.getEmailsFromExcel(tempFile);
            emailService.sendEmails(emails, subject, message);

            if (emails.isEmpty()) {
                return "Nenhum e-mail válido encontrado.";
            } else {
                return emails.size() + " e-mails válidos, enviados com sucesso!";
            }
        } finally {
            // Exclui o arquivo temporário, garantindo que sempre será removido
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
