package it.cgmconsulting.myblog.payload.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class SigninRequest {

    @NotBlank
    @Size(min = 5)
    private String usernameOrEmail;

    @NotBlank
    @Size(min = 5, max = 15)
    private String password;

}
