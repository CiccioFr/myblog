package it.cgmconsulting.myblog.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * DTO
 */
@Setter @Getter @NoArgsConstructor
public class JwtAuthenticationResponse {

	private long id;
	private String username;
	private String email;
	private Set<String> authorities;
	private String token;
	
	public JwtAuthenticationResponse(long id, String username, String email, Set<String> authorities, String token) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.authorities = authorities;
		// il suffisso Bearer serve a Spring Security che tipo di schema viene usato
		// è uno standard e quasi tutti usano questo
		this.token = "Bearer "+token;
	}
}
