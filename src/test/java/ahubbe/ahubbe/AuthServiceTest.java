package ahubbe.ahubbe;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.repository.UserRepository;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원가입시 데이터가 DB에 잘 저장되는지 확인")
    void registerTest() {
        authService.registerUser("testId", "testPassword");

        Assertions.assertThat(userRepository.findById("testId").isPresent()).isTrue();
    }

    @Test
    @DisplayName("중복된 ID가 존재할 시, 잘 막히는지 확인")
    void registerDuplicateTest() {
        try {
            authService.registerUser("testId", "testPassword");
            authService.registerUser("testId", "testPassword");
            Assertions.fail("중복 아이디이지만 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage()).contains("이미 사용 중인 아이디입니다.");
        }
    }

    @Test
    @DisplayName("JWT 토큰이 잘 발급되는지 확인")
    void jwtTokenTest() {
        authService.registerUser("testId", "testPassword");
        JwtToken jwtToken = authService.signIn("testId", "testPassword");

        Assertions.assertThat(jwtToken).isNotNull();
        Assertions.assertThat(jwtToken.getGrantType()).isEqualTo("Bearer");
        Assertions.assertThat(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).isTrue();
    }
}
