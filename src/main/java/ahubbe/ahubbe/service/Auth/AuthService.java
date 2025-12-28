package ahubbe.ahubbe.service.Auth;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.entity.Role;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(String id, String password) {
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User newUser = new User();
        newUser.setId(id);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(Role.ROLE_USER);
        userRepository.save(newUser);
    }

    public JwtToken signIn(String id, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(id, password);

        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }
}
