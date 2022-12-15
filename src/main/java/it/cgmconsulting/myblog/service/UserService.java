package it.cgmconsulting.myblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
	
	@Autowired UserRepository userRepository;
	
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public void save(User user) {
		userRepository.save(user);
	}

    public Optional<User> findById(long id) {
		return userRepository.findById(id);
    }

	public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }
	public Optional<User> findByUsernameOrEmail(String username, String email) { return userRepository.findByUsernameOrEmail(username,email); }

}
