package it.cgmconsulting.myblog.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.stereotype.Component;

/**
 *
 *
 * REFUSO
 *
 *
 * Non serve in questa App
 */
@Slf4j
@Component
public class DisableListener implements ApplicationListener<AuthenticationFailureDisabledEvent> {

    private String userName;
    private String credentials;

    @Override
    public void onApplicationEvent(AuthenticationFailureDisabledEvent event) {
        userName = (String) event.getAuthentication().getPrincipal();
        credentials = (String) event.getAuthentication().getCredentials();
        log.info("Failed login using USERNAME [" + userName + "]");
        log.info("Failed login using PASSWORD [" + credentials + "]");

    }

    public String getUserName(){
        return userName;
    }

    public String getCredentials(){
        return credentials;
    }

    public void setUserNameNull(){
        this.userName=null;
    }

}
