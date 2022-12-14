package it.cgmconsulting.myblog.mail;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Mail {

    private String mailFrom;
    private String mailTo;
    private String mailSubject;
    private String mailContent;
    // html/txt - sia html che testo semplice
    private String mailMimeType;
}
