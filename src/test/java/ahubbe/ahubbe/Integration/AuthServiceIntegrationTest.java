package ahubbe.ahubbe.Integration;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.repository.AuthRepository;
import ahubbe.ahubbe.repository.UserRepository;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceIntegrationTest {

    @Autowired private AuthService authService;

    @Autowired private UserRepository userRepository;

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private AuthRepository authRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        authService.registerUser("baseUser", "password", "base@email.com");
    }

    @Test
    @DisplayName("회원가입시 데이터가 DB에 잘 저장되는지 확인")
    void registerTest() {
        authService.registerUser("testId", "testPassword", "test@email.com");

        Assertions.assertThat(userRepository.findById("testId").isPresent()).isTrue();
    }

    @Test
    @DisplayName("중복된 ID가 존재할 시, 잘 막히는지 확인")
    void registerDuplicateTest() {
        try {
            authService.registerUser("baseUser", "password", "base@email.com");
            Assertions.fail("중복 아이디이지만 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage()).contains("이미 사용 중인 아이디입니다.");
        }
    }

    @Test
    @DisplayName("정상적으로 로그인이 되는지 확인")
    void loginTest() {
        JwtToken token = authService.signIn("baseUser", "password");

        Assertions.assertThat(token).isNotNull();
        Assertions.assertThat(token.getAccessToken()).isNotEmpty();
        Assertions.assertThat(token.getGrantType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("존재하지 않는 유저로 로그인 시 실패하는 것 확인")
    void loginFailNonExistUserTest() {
        Assertions.assertThatThrownBy(
                        () -> {
                            authService.signIn("ghost", "ghost");
                        })
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("유저가 정상적으로 탈퇴되는지 확인")
    void resignTest() {
        Assertions.assertThat(authService.resignUser("baseUser")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 유저로 탈퇴 시 False 반환하는 것 확인")
    void resignFailNonExistUserTest() {
        Assertions.assertThat(authService.resignUser("ghost")).isFalse();
    }

    @Test
    @DisplayName("유저의 비밀번호가 정상적으로 바뀌는지 확인")
    void changePasswordTest() {
        Assertions.assertThat(authService.changePassword("baseUser", "newPassword")).isTrue();
        String savedPassword = userRepository.findById("baseUser").get().getPassword();
        Assertions.assertThat(passwordEncoder.matches("newPassword", savedPassword)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 유저의 비밀번호 변경 시도시 False 반환하는 것 확인")
    void changePasswordFailNonExistUserTest() {
        Assertions.assertThat(authService.changePassword("ghost", "newPassword")).isFalse();
    }

    @Test
    @DisplayName("아이디에 해당하는 비밀번호인지 확인")
    void checkUserTest() {
        Assertions.assertThat(authService.checkUser("baseUser", "password")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 유저의 비밀번호를 확인하려 할 경우 False 반환하는 것 확인")
    void checkUserFailNonExistUserTest() {
        Assertions.assertThatThrownBy(
                        () -> {
                            authService.checkUser("ghost", "newPassword");
                        })
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("유저가 있지만 비밀번호를 틀린 경우")
    void checkUserButWrongPasswordTest() {
        Assertions.assertThat(authService.checkUser("baseUser", "badpassword")).isFalse();
    }

    @Test
    @DisplayName("해당하는 이메일을 가진 유저가 있는 경우")
    void checkEmail() {
        Assertions.assertThat(authService.emailCheck("base@email.com")).isTrue();
    }

    @Test
    @DisplayName("해당하는 이메일을 가진 유저가 없는 경우")
    void checkNonExistEmail() {
        Assertions.assertThat(authService.emailCheck("ghost@email.com")).isFalse();
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 유저가 있는 경우")
    void checkId() {
        Assertions.assertThat(authService.idCheck("baseUser")).isTrue();
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 유저가 없는 경우")
    void checkNonExistId() {
        Assertions.assertThat(authService.idCheck("ghost")).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰이 잘 발급되는지 확인")
    void jwtTokenTest() {
        authService.registerUser("testId", "testPassword", "test@email.com");
        JwtToken jwtToken = authService.signIn("testId", "testPassword");

        Assertions.assertThat(jwtToken).isNotNull();
        Assertions.assertThat(jwtToken.getGrantType()).isEqualTo("Bearer");
        Assertions.assertThat(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).isTrue();
    }
}
