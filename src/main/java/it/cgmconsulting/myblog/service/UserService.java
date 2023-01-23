package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.response.UserMe;
import it.cgmconsulting.myblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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

    public Optional<User> findByIdAndEnabledTrue(long id) {
        return userRepository.findByIdAndEnabledTrue(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public Optional<User> findByConfirmCode(String confirmCode) {
        return userRepository.findByConfirmCode(confirmCode);
    }

    public Optional<User> findByUsernameAndEnabledTrue(String username) {
        return userRepository.findByUsernameAndEnabledTrue(username);
    }

    public UserMe getMe(long id) {
        return userRepository.getMe(id);
    }

    // todo
    void disableUser(long userId){

    }

    // ************ GENERATORE RANDOM DI PASSWORD ************ //
    Random random = new SecureRandom();

    public String generateSecureRandomPassword() {
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2),
                // commento per non avere caratteri speciali nella PW
                /*Stream.concat(getRandomSpecialChars(2),*/
                Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))
        );
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList);
        String password = charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

    public Stream<Character> getRandomNumbers(int count) {
        IntStream numbers = random.ints(count, 48, 57);
        return numbers.mapToObj(data -> (char) data);
    }

    public Stream<Character> getRandomSpecialChars(int count) {
        IntStream specialChars = random.ints(count, 33, 45);
        return specialChars.mapToObj(data -> (char) data);
    }

    public Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
        IntStream characters = null;
        if (upperCase) {
            characters = random.ints(count, 65, 90);
        } else {
            characters = random.ints(count, 97, 122);
        }
        return characters.mapToObj(data -> (char) data);
    }
    // ************************ //
}
