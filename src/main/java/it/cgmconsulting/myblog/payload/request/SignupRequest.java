package it.cgmconsulting.myblog.payload.request;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * per la registrazione dell'utente
 */
@Getter
public class SignupRequest {

    /**
     * @NotBlank annotazione di validazione per controlli dati in input
     * l'identificativo della variabile DEVE corrispondere come key nel Json
     */
    @NotBlank
    @Size(max = 20, min = 5)
    private String username;

    @NotBlank
    @Email
    private String email;

    /**
     * Qui arriva la Password in chiaro
     */
    @NotBlank @Size(min = 6)
    // per ReGex
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$",
            message = "Password deve contenere tra 5 e 20 caratteri" +
                    "\nPassword must be of 5 to 15 length with no special characters\n")
    private String password;

}
