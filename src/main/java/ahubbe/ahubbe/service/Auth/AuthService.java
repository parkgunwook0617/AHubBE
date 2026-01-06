package ahubbe.ahubbe.service.Auth;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.entity.Role;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(String id, String password, String email) {
        if (userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User newUser = new User();
        newUser.setId(id);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(Role.ROLE_USER);
        newUser.setEmail(email);
        userRepository.save(newUser);
    }

    @Transactional
    public JwtToken signIn(String id, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(id, password);

        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setRefreshToken(jwtToken.getRefreshToken());

        return jwtToken;
    }

    @Transactional
    public JwtToken reissue(String refreshToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다.");
        }

        JwtToken newTokens = jwtTokenProvider.generateToken(authentication);

        user.setRefreshToken(newTokens.getRefreshToken());

        return newTokens;
    }

    @Transactional
    public void signOut(String id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setRefreshToken(null);
    }

    @Transactional
    public boolean resignUser(String id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) return false;

        userRepository.delete(user);
        return true;
    }

    @Transactional
    public boolean changePassword(String id, String newPassword) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    public boolean checkUser(String id, String password) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        if (user == null) return false;

        return passwordEncoder.matches(password, user.getPassword());
    }

    public boolean emailCheck(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean idCheck(String id) {
        return userRepository.findById(id).isPresent();
    }
}
