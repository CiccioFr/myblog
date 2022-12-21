package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * la usiamo quando il risultato restituisce una parte dei campi
 * (un sottoinsieme di tabella)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private String categoryname;
    private boolean visible;
}
