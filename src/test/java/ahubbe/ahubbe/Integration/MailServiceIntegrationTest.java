package ahubbe.ahubbe.Integration;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import ahubbe.ahubbe.entity.Auth;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.AuthRepository;
import ahubbe.ahubbe.repository.UserRepository;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MailServiceIntegrationTest {
    @Autowired private MailService mailService;

    @Autowired private AuthRepository authRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private JavaMailSender mailSender;

    @Autowired private UserRepository userRepository;

    @Autowired private AuthService authService;

    @Test
    @DisplayName("인증메일 발송 시 DB에 인증 코드가 정상적으로 갱신되는지 확인")
    void sendSimpleMessageTest() throws MessagingException {
        String email = "test@example.com";

        authRepository.save(new Auth(email, "OLD_CODE"));

        MimeMessage mockMessage = mock(MimeMessage.class);
        given(mailSender.createMimeMessage()).willReturn(mockMessage);

        boolean result = mailService.sendSimpleMessage(email);

        Assertions.assertThat(result).isTrue();

        Auth savedAuth = authRepository.findByEmail(email);
        Assertions.assertThat(savedAuth).isNotNull();
        Assertions.assertThat(savedAuth.getAuthCode()).isNotEqualTo("OLD_CODE");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("비밀번호 초기화 메일 발송 시 Auth 테이블에 토큰과 임시비밀번호가 저장되는 것 확인")
    void sendResetMailTest() throws MessagingException {
        String email = "test@gmail.com";
        authService.registerUser("testUser1234", "testPass!23", email);

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        mailService.sendResetMail(email);

        Auth savedAuth = authRepository.findByEmail(email);

        Assertions.assertThat(savedAuth).isNotNull();
        Assertions.assertThat(savedAuth.getToken()).isNotNull();
        Assertions.assertThat(savedAuth.getTempPassword()).isNotNull();

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("비밀번호 업데이트 시 암호화된 비밀번호가 DB에 반영되는 것 확인")
    void updatePasswordTest() {
        String email = "test@gmail.com";
        String oldRawPassword = "oldPassword123!";
        String newRawPassword = "newPassword5678!";

        authService.registerUser("testUser", oldRawPassword, email);

        mailService.updatePassword(email, newRawPassword);

        User updatedUser = userRepository.findByEmail(email).orElseThrow();

        Assertions.assertThat(updatedUser.getPassword()).isNotEqualTo(newRawPassword);
        boolean isMatch = passwordEncoder.matches(newRawPassword, updatedUser.getPassword());
        Assertions.assertThat(isMatch).isTrue();

        Assertions.assertThat(passwordEncoder.matches(oldRawPassword, updatedUser.getPassword()))
                .isFalse();
    }

    @Test
    @DisplayName("인증 번호가 일치하면 정보를 삭제한는 것 확인")
    void validateCodeSuccessTest() {
        String email = "test@gmail.com";
        String correctCode = "123456";
        authRepository.save(new Auth(email, correctCode));

        boolean result = mailService.validateCode(email, correctCode);

        Assertions.assertThat(result).isTrue();

        Auth deletedAuth = authRepository.findByEmail(email);
        Assertions.assertThat(deletedAuth).isNull();
    }

    @Test
    @DisplayName("인증 번호가 일치하지 않으면 데이터를 삭제하지 않는 것 확인")
    void validateCodeFailTest() {
        String email = "wrong@test.com";
        authRepository.save(new Auth(email, "123456"));

        boolean result = mailService.validateCode(email, "000000");

        Assertions.assertThat(result).isFalse();

        Auth remainingAuth = authRepository.findByEmail(email);
        Assertions.assertThat(remainingAuth).isNotNull();
    }

    @Test
    @DisplayName("올바른 토큰을 입력하면 비밀번호가 변경되고 Auth 데이터가 삭제되어야 한다")
    void verifyTokenAndResetPasswordSuccessTest() {
        String email = "reset@test.com";
        String token = "uuid-token-1234";
        String tempPassword = "TempPassword123!";

        authService.registerUser("resetUser1", "oldPassword!23", email);
        authRepository.save(new Auth(email, token, tempPassword));

        boolean result = mailService.verifyTokenAndResetPassword(token);

        Assertions.assertThat(result).isTrue();

        User user = userRepository.findByEmail(email).orElseThrow();
        boolean isPasswordMatch = passwordEncoder.matches(tempPassword, user.getPassword());
        Assertions.assertThat(isPasswordMatch).isTrue();

        Auth deletedAuth = authRepository.findByToken(token);
        Assertions.assertThat(deletedAuth).isNull();
    }
}
