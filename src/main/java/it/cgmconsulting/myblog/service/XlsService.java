package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.payload.response.XlsAuthorResponse;
import it.cgmconsulting.myblog.payload.response.XlsPostResponse;
import it.cgmconsulting.myblog.payload.response.XlsReaderResponse;
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

        // generiamo un byte perche il file è un .. di byte
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // creazione xls - Inizializzo Figlio Excel
        HSSFWorkbook workbook = new HSSFWorkbook();

        // creazione sheets (schede del foglio Excel) ed associazione al workbook
        createAuthorReport(workbook);
        createReaderReport(workbook);
        createPostReport(workbook);

        // nel moneto in cui facciamo la close (non stiamo scrivendo nel file system)
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
        Cell cell;

        row = sheet.createRow(rownum);

        // Intestazione delle colonne
        String[] labels = {"Id", "username", "Nr. Post Written", "Average rate post"};
        for (String s : labels) {
            cell = row.createCell(columns++, CellType.STRING);
            cell.setCellValue(s);
        }

        List<XlsAuthorResponse> list = userRepository.getXlsAuthorResponse();
        for (XlsAuthorResponse x : list) {
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
        int rownum=0;
        int column=0;
        Row row;
        Cell cell;

        row = sheet.createRow(rownum);

        // intestazione delle colonne
        String[] labels = {"Id", "username", "Nr. of comments", "Nr. reporting with ban", "Enabled(Y/N)"};
        for(String s : labels){
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(s);
        }

        List<XlsReaderResponse> list = userRepository.getXlsReaderResponse();
        int totalComments = 0;
        int totalBans = 0;
        for(XlsReaderResponse x : list) {
            column = 0;
            row = sheet.createRow(++rownum);
            // Id
            cell = row.createCell(column++, CellType.NUMERIC);
            cell.setCellValue(x.getId());
            // Username
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(x.getUsername());
            // Nr. comments written
            cell = row.createCell(column++, CellType.NUMERIC);
            cell.setCellValue(x.getWrittenComments());
            totalComments += x.getWrittenComments();
            // Nr. reporting with Ban
            cell = row.createCell(column++, CellType.NUMERIC);
            cell.setCellValue(x.getReportingsWithBan());
            totalBans += x.getReportingsWithBan();
            // Enabled (Y/N)
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(x.isEnabled() ? "Y" : "N");
        }
        // TOTALS
        row.setRowNum(row.getRowNum()+1);
        column = 0;

        setEmptyCell(column++, row);
        cell = row.createCell(column++, CellType.NUMERIC);
        cell.setCellValue(list.size());
        cell = row.createCell(column++, CellType.NUMERIC);
        cell.setCellValue(totalComments);
        cell = row.createCell(column++, CellType.NUMERIC);
        cell.setCellValue(totalBans);
        setEmptyCell(column++, row);
    }

    public void createPostReport(HSSFWorkbook workBook) {
        HSSFSheet sheet = workBook.createSheet("Post Report");
        int rownum = 0;
        int column = 0;
        Row row;
        Cell cell;
        row = sheet.createRow(rownum); // All'interno del mio sheet comincia a scrivere dalla riga numero 0;
        //intestazione delle colonne.

        String[] labels = {"Id", "Title", "Average", "Published (Y/N)", "Author"};
        for (String s : labels) {
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(s);
        }
        List<XlsPostResponse> list = postRepository.getXlsPostResponse();
        int countPublished = 0;
        for (XlsPostResponse x : list) {
            column = 0;
            row = sheet.createRow(++rownum);
            // post id
            cell = row.createCell(column++, CellType.NUMERIC);
            cell.setCellValue(x.getId());
            // post title
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(x.getTitle());
            // post avg
            cell = row.createCell(column++, CellType.NUMERIC);
            cell.setCellValue(x.getAvg());
            // published (Y/N)
            cell = row.createCell(column++, CellType.STRING);
            if (x.isPublished()) {
                cell.setCellValue("Y");
                countPublished++;
            } else {
                cell.setCellValue("N");
            }
            // Author username
            cell = row.createCell(column++, CellType.STRING);
            cell.setCellValue(x.getAuthor());
        }
        // TOTALS
        row.setRowNum(row.getRowNum() + 1);
        column = 0;

        setEmptyCell(column++, row);
        cell = row.createCell(column++, CellType.NUMERIC);
        cell.setCellValue(list.size());
        setEmptyCell(column++, row);
        cell = row.createCell(column++, CellType.NUMERIC);
        cell.setCellValue(countPublished);
    }

    private Cell setEmptyCell(int column, Row row) {
        Cell cell;
        cell = row.createCell(column++, CellType.STRING);
        cell.setCellValue("");
        return cell;
    }
}
