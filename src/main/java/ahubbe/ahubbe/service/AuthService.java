package ahubbe.ahubbe.service;

import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(String id, String password) {
        User newUser = new User();

        newUser.setId(id);
        newUser.setPassword(password);

        userRepository.save(newUser);
    }
}
