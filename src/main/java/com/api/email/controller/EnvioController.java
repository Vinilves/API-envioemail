package com.api.email.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.email.service.EmailService;
import com.api.email.service.ExcelService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

@Service
public class EnvioController {

    @FXML
    private TextField TextAssunto;

    @FXML
    private TextArea TextMensagem;

    @FXML
    private Button btnAdicionarArquivo;

    @FXML
    private Button btnEnviar;

    @FXML
    private File selectedFile;

    @Autowired
    private ExcelService excelService; // Injeta o serviço de leitura de Excel

    @Autowired
    private EmailService emailService; // Injeta o serviço de envio de e-mail

    @FXML
    public void initialize() {
        btnAdicionarArquivo.setOnAction(e -> adicionarArquivo());
        btnEnviar.setOnAction(e -> enviarArquivo());
    }

    @FXML
    public void adicionarArquivo() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) btnAdicionarArquivo.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            System.out.println("Arquivo selecionado: " + selectedFile.getName());
        } else {
            System.out.println("Nenhum arquivo selecionado.");
        }
    }

    @FXML
    public void enviarArquivo() {
        String assunto = TextAssunto.getText();
        String descricao = TextMensagem.getText();

        if (selectedFile != null) {
            try {
                // Extrai os e-mails do arquivo Excel
                List<String> emails = excelService.getEmailsFromExcel(selectedFile);

                // Envia e-mails apenas se a lista não estiver vazia
                if (!emails.isEmpty()) {
                    emailService.sendEmails(emails, assunto, descricao); // Envia e-mails utilizando o EmailService
                    System.out.println(emails.size() + " e-mails enviados com sucesso!");
                } else {
                    System.out.println("Nenhum e-mail válido encontrado no arquivo."); // Exibe um alerta informando que não foram encontrados e-mails
                }
            } catch (IOException e) {
                e.printStackTrace(); // Exibe um alerta informando erro na leitura do arquivo
            } catch (MessagingException e) {
                e.printStackTrace(); // Exibe um alerta informando erro no envio do e-mail
            }
        } else {
            System.out.println("Nenhum arquivo selecionado."); // Exibe um alerta informando que o usuário deve selecionar um arquivo
        }
    }
}
