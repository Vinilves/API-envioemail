package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    // Valida o formato de e-mails
    public boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    // Lê os e-mails de um arquivo Excel
    public List<String> getEmailsFromExcel(File file) throws IOException {
        List<String> emails = new ArrayList<>();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            fis = new FileInputStream(file);
            workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0); // Primeira aba da planilha

            for (Row row : sheet) {
                Cell cell = row.getCell(1); // Segunda coluna, onde os e-mails estão

                // Verifica se a célula não é nula e se é do tipo STRING
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String email = cell.getStringCellValue().trim(); // Remove espaços em branco

                    // Se o texto na célula for um e-mail válido, adiciona à lista
                    if (isValidEmail(email)) {
                        emails.add(email);
                    }
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (fis != null) {
                fis.close();
            }
        }

        // Exibe uma mensagem se nenhum e-mail válido foi encontrado
        if (emails.isEmpty()) {
            System.out.println("Nenhum e-mail válido encontrado.");
        }

        return emails; // Retorna os e-mails válidos
    }
}
