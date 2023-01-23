package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.repository.UserRepository;
import it.cgmconsulting.myblog.repository.PostRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class XlsService {

    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;

    public InputStream createReport() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // creazione xls
        HSSFWorkbook workbook = new HSSFWorkbook();

        // creazione sheet
        createAuthorReport(workbook);
        createReaderReport(workbook);
        createPostReport(workbook);

        workbook.write(out);
        workbook.close();

        InputStream in = new ByteArrayInputStream(out.toByteArray());

        return in;
    }


    public void createAuthorReport(HSSFWorkbook workbook){

    }

    public void createPostReport(HSSFWorkbook workBook) {

    }

    public void createReaderReport(HSSFWorkbook workBook) {

    }

}
