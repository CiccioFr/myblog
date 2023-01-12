package it.cgmconsulting.myblog.payload.response;

import it.cgmconsulting.myblog.entity.Avatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserMe {

    private long id;
    private String username;
    private String email;
    private Avatar avatar;
}
