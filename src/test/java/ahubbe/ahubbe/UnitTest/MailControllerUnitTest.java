package ahubbe.ahubbe.UnitTest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import ahubbe.ahubbe.controller.MailController;
import ahubbe.ahubbe.dto.EmailDto;
import ahubbe.ahubbe.dto.ValidateEmailDto;
import ahubbe.ahubbe.service.Mail.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class MailControllerUnitTest {
    private MockMvc mockMvc;
    private MailService mailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mailService = mock(MailService.class);
        MailController mailController = new MailController(mailService);

        mockMvc = standaloneSetup(mailController).build();
    }

    @Test
    @DisplayName("인증 코드 전송 성공 시 서비스 메서드가 호출되는 것 확인")
    void requestAuthcodeTest() throws Exception {
        String email = "test@gmail.com";

        given(mailService.sendSimpleMessage("test@gmail.com")).willReturn(true);

        mockMvc.perform(get("/mail/{email}", email)).andExpect(status().isOk());

        verify(mailService).sendSimpleMessage("test@gmail.com");
    }

    @Test
    @DisplayName("인증 코드 전송 실패 시 서비스 메서드가 호출되는 것 확인")
    void requestAuthcodeFailTest() throws Exception {
        String email = "test@gmail.com";

        given(mailService.sendSimpleMessage("test@gmail.com")).willReturn(false);

        mockMvc.perform(get("/mail/{email}", email)).andExpect(status().isBadRequest());

        verify(mailService).sendSimpleMessage("test@gmail.com");
    }

    @Test
    @DisplayName("코드 검증 성공시 서비스 메서드가 호출되는 것 확인")
    void validEmailTest() throws Exception {
        ValidateEmailDto validateEmailDto = new ValidateEmailDto("test@gmail.com", "123456");

        given(mailService.validateCode("test@gmail.com", "123456")).willReturn(true);

        mockMvc.perform(
                        post("/mail/validatemail")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validateEmailDto)))
                .andExpect(status().isOk());

        verify(mailService).validateCode("test@gmail.com", "123456");
    }

    @Test
    @DisplayName("코드 검증 실패시 서비스 메서드가 호출되는 것 확인")
    void validEmailFailTest() throws Exception {
        ValidateEmailDto validateEmailDto = new ValidateEmailDto("test@gmail.com", "123456");

        given(mailService.validateCode("test@gmail.com", "123456")).willReturn(false);

        mockMvc.perform(
                        post("/mail/validatemail")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validateEmailDto)))
                .andExpect(status().isBadRequest());

        verify(mailService).validateCode("test@gmail.com", "123456");
    }

    @Test
    @DisplayName("비밀번호 초기화 성공시 서비스 메서드가 호출되는 것 확인")
    void executeResetTest() throws Exception {
        String token = "valid-token";

        given(mailService.verifyTokenAndResetPassword(token)).willReturn(true);

        mockMvc.perform(get("/mail/reset-execute").param("token", token))
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .string(
                                        matchesPattern(
                                                "<h1>.*</h1><p>.*<b>.*</b>.*</p><a href='/login'>.*</a>")));

        verify(mailService).verifyTokenAndResetPassword(token);
    }

    @Test
    @DisplayName("비밀번호 초기화 실패시 서비스 메서드가 호출되는 것 확인")
    void executeResetFailTest() throws Exception {
        String token = "valid-token";

        given(mailService.verifyTokenAndResetPassword(token)).willReturn(false);

        mockMvc.perform(get("/mail/reset-execute").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("<h1>.*</h1><p>.*</p>")));

        verify(mailService).verifyTokenAndResetPassword(token);
    }

    @Test
    @DisplayName("토큰이 누락된 경우 반환 확인")
    void executeResetNoTokenTest() throws Exception {
        mockMvc.perform(get("/mail/reset-execute"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("<h1>.*</h1><p>.*</p>")));
    }

    @Test
    @DisplayName("토큰이 빈 문자열인 경우 반환 확인")
    void executeResetEmptyTokenTest() throws Exception {
        mockMvc.perform(get("/mail/reset-execute").param("token", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("<h1>.*</h1><p>.*</p>")));
    }

    @Test
    @DisplayName("비밀번호 초기화 예외처리시 서비스 메서드가 호출되는 것 확인")
    void executeResetExceptionTest() throws Exception {
        String token = "valid-token";

        given(mailService.verifyTokenAndResetPassword(token)).willThrow(new RuntimeException());

        mockMvc.perform(get("/mail/reset-execute").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("<h1>.*</h1><p>.*</p>")));

        verify(mailService).verifyTokenAndResetPassword(token);
    }

    @Test
    @DisplayName("비밀번호 초기화 메일 발신시 서비스 메서드가 호출되는 것 확인")
    void requestPasswordResetTest() throws Exception {
        EmailDto emailDto = new EmailDto("test@gmail.com");

        mockMvc.perform(
                        post("/mail/reset-password-request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isOk());

        verify(mailService).sendResetMail("test@gmail.com");
    }

    @Test
    @DisplayName("비밀번호 초기화 메일 발신시 에러 타입이 IllegalArgumentException일 때 서비스 메서드가 호출되는 것 확인")
    void requestPasswordResetErrorType1Test() throws Exception {
        EmailDto emailDto = new EmailDto("test@gmail.com");

        doThrow(new IllegalArgumentException("해당 이메일의 사용자가 없습니다."))
                .when(mailService)
                .sendResetMail("test@gmail.com");

        mockMvc.perform(
                        post("/mail/reset-password-request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isNotFound());

        verify(mailService).sendResetMail("test@gmail.com");
    }

    @Test
    @DisplayName("비밀번호 초기화 메일 발신시 에러 타입이 Exception일 때 서비스 메서드가 호출되는 것 확인")
    void requestPasswordResetErrorType2Test() throws Exception {
        EmailDto emailDto = new EmailDto("test@gmail.com");

        doThrow(new RuntimeException()).when(mailService).sendResetMail("test@gmail.com");

        mockMvc.perform(
                        post("/mail/reset-password-request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isInternalServerError());

        verify(mailService).sendResetMail("test@gmail.com");
    }
}
