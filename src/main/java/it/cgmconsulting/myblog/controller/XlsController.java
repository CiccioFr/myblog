package it.cgmconsulting.myblog.controller;


import it.cgmconsulting.myblog.service.XlsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("xls")
public class XlsController {

    @Autowired
    XlsService xlsService;

    // la chiamata GET "localhost:8083/xls/public" genera un file Excel
    @GetMapping("public")
    public ResponseEntity<?> createReport() {

        InputStream xlsFile = null;
        ResponseEntity<InputStreamResource> responseEntity = null;

        try {
            // xlsService.createReport() genererà effettivamente un file Excel
            xlsFile = xlsService.createReport();
            // Il Fornt-End capirà da queste info che tipo di file gli sto passando
            HttpHeaders headers = new HttpHeaders();
            // passaggio del MimeType
            // il mimetype permette di associare una applicazione al file
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            // VINCOLI SULL'ORIGINE DEL CHIAMANTE:
            // le chiamate possono arrivare da qualunque IP / un singolo / range IP
            headers.add("Access-Control-Allow-Origin", "*");
            // unico metodo che permettiamo, get per recuperare
            headers.add("Access-Control-Allow-Method", "GET");
            // cont tipe la parte della chash, nel momento della generezaione, non tenerlo in chash,
            // perche potrebbe non essere aggiornato (es il gg dopo)
            // per rivalidare il file e rigenerato un nuovo file
            headers.add("Access-Control-Allow-Header", "Content-Type");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            // impostiamo nome file
            headers.add("Content-disposition", "inline; filename=Report.xls");

            // la respons di tipo InputStreamResource, che contiene il file
            responseEntity = new ResponseEntity<InputStreamResource>(
                    new InputStreamResource(xlsFile),
                    headers,
                    HttpStatus.OK
            );

        } catch (Exception ex) {
            responseEntity = new ResponseEntity<InputStreamResource>(
                    new InputStreamResource(null, "Error creating Report"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return responseEntity;

    }

}
