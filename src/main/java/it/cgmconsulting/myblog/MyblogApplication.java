package it.cgmconsulting.myblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/* - by Mattia - per bypassare tutte le sicurezze, anche password
	@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) */
@SpringBootApplication
@EnableAsync
public class MyblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyblogApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
