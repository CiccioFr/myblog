package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.payload.response.XlsAuthorResponse;
import it.cgmconsulting.myblog.repository.PostRepository;
import it.cgmconsulting.myblog.repository.UserRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class XlsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;

    public InputStream createReport() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // creazione xls - Inizializzo Figlio Excel
        HSSFWorkbook workbook = new HSSFWorkbook();

        // creazione sheet (schede del foglio Excel) ed associazione al workbook
        createAuthorReport(workbook);
        createReaderReport(workbook);
        //createPostReport(workbook);

        workbook.write(out);
        workbook.close();

        // ByteArrayOutputStream() viene convertito in un InputStream, che viene passato al Controller
        InputStream in = new ByteArrayInputStream(out.toByteArray());

        return in;
    }

    // tre metodi per generare i fogli Excel
    // uun metodo per ogni foglio che voglio generare nel file excel
    public void createAuthorReport(HSSFWorkbook workbook) {
        // 1 inizializziamo lo sheet (foglio / Scheda)
        HSSFSheet sheet = workbook.createSheet("Author Report");
        // passiamo righe (A, B, ..) e colonne (1, 2, ..)
        // setto la riga di partenza (parte da 0)
        int rownum = 0;
        // setto la colonna di partenza (parte da 0)
        int columns = 0;

        Row row;
        row = sheet.createRow(rownum);
        Cell cell;

        // Intestazione delle colonne
        String[] labels = {"Id", "username", "Nr. Post Written", "Average rate post"};
        for (String s : labels){
            cell = row.createCell(columns++, CellType.STRING);
            cell.setCellValue(s);
        }

        List<XlsAuthorResponse> list = userRepository.getXlsAuthorResponse();
        for (XlsAuthorResponse x : list){
            row = sheet.createRow(++rownum);
            // id
            cell = row.createCell(columns++, CellType.NUMERIC);
            cell.setCellValue(x.getId());
            // UserName
            cell = row.createCell(columns++, CellType.STRING);
            cell.setCellValue(x.getUsername());
            // Posts Written
            cell = row.createCell(columns++, CellType.NUMERIC);
            cell.setCellValue(x.getWrittenPost());
            // Average
            cell = row.createCell(columns++, CellType.NUMERIC);
            cell.setCellValue(x.getAvg());
        }
    }

    public void createReaderReport(HSSFWorkbook workBook) {
        HSSFSheet sheet = workBook.createSheet("Reader Report");
        int rownum = 0;
        int columns = 0;
        Row row;
        Cell cell;

        row = sheet.createRow(rownum);

        String[] labels = {"Id", "username", "Nr. of comments", "Nr. reporting with ban", "Enabled (Y/N)"};
        for (String s : labels){
            cell = row.createCell(columns++, CellType.STRING);
            cell.setCellValue(s);
        }    }

    public void createPostReport(HSSFWorkbook workBook) {

    }

}
