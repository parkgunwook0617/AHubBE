package ahubbe.ahubbe.UnitTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import ahubbe.ahubbe.controller.AuthController;
import ahubbe.ahubbe.dto.AuthDto;
import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.dto.PasswordDto;
import ahubbe.ahubbe.dto.RegisterDto;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

public class AuthControllerUnitTest {
    private MockMvc mockMvc;
    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        authService = mock(AuthService.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthController authController = new AuthController(authService, jwtTokenProvider);

        mockMvc = standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("회원가입시 서비스 메서드가 잘 호출되는지 확인")
    void registerTest() throws Exception {
        RegisterDto registerDto = new RegisterDto("testuser12", "testpass123!", "test@gmail.com");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());

        verify(authService).registerUser("testuser12", "testpass123!", "test@gmail.com");
    }

    @Test
    @DisplayName("로그인 성공 시 쿠키가 잘 등록되는지 확인")
    void signInSuccessTest() throws Exception {
        JwtToken mockToken =
                JwtToken.builder()
                        .grantType("Bearer")
                        .accessToken("access-123")
                        .refreshToken("refresh-456")
                        .build();

        AuthDto requestDto = new AuthDto("testuser12", "testpass123!");

        given(authService.signIn("testuser12", "testpass123!")).willReturn(mockToken);

        mockMvc.perform(
                        post("/auth/signIn")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("accessToken=access-123"))))
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("refreshToken=refresh-456"))));

        verify(authService).signIn("testuser12", "testpass123!");
    }

    @Test
    @DisplayName("로그인 시도시 아이디, 비밀번호가 틀렸을 시 잘 처리되는지 확인")
    void signInFailTest() throws Exception {
        AuthDto requestDto = new AuthDto("testuser12", "testpass123!");

        given(authService.signIn("testuser12", "testpass123!"))
                .willThrow(new BadCredentialsException("아이디 또는 비밀번호가 틀렸습니다."));

        mockMvc.perform(
                        post("/auth/signIn")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());

        verify(authService).signIn("testuser12", "testpass123!");
    }

    @Test
    @DisplayName("로그인 시도시 서버 오류시 틀렸을 시 잘 처리되는지 확인")
    void signInFailServerTest() throws Exception {
        AuthDto requestDto = new AuthDto("testuser12", "testpass123!");

        given(authService.signIn("testuser12", "testpass123!"))
                .willThrow(new RuntimeException("서버 오류입니다."));

        mockMvc.perform(
                        post("/auth/signIn")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());

        verify(authService).signIn("testuser12", "testpass123!");
    }

    @Test
    @DisplayName("재로그인 성공 시 쿠키가 잘 등록되는지 확인")
    void reSignInSuccessTest() throws Exception {
        JwtToken mockToken =
                JwtToken.builder()
                        .grantType("Bearer")
                        .accessToken("access-123")
                        .refreshToken("refresh-456")
                        .build();

        String testRefreshToken = "refresh-123";

        given(jwtTokenProvider.validateToken(testRefreshToken)).willReturn(true);
        given(authService.reissue(testRefreshToken)).willReturn(mockToken);

        mockMvc.perform(post("/auth/reissue").cookie(new Cookie("refreshToken", testRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("accessToken=access-123"))))
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("refreshToken=refresh-456"))));

        verify(authService).reissue("refresh-123");
    }

    @Test
    @DisplayName("재로그인 실패 시 잘 처리되는지 확인")
    void reSignInFailTest() throws Exception {
        String testRefreshToken = "refresh-123";

        given(jwtTokenProvider.validateToken(testRefreshToken)).willReturn(true);
        given(authService.reissue(testRefreshToken)).willThrow(new RuntimeException());

        mockMvc.perform(post("/auth/reissue").cookie(new Cookie("refreshToken", testRefreshToken)))
                .andExpect(status().isUnauthorized());

        verify(authService).reissue("refresh-123");
    }

    @Test
    @DisplayName("재로그인 레프레시 토큰인증 오류시 처리가 잘 되는지 확인")
    void reSignInFailByInvalidTokenTest() throws Exception {
        String testRefreshToken = "refresh-123";

        given(jwtTokenProvider.validateToken(testRefreshToken)).willReturn(false);

        mockMvc.perform(post("/auth/reissue").cookie(new Cookie("refreshToken", testRefreshToken)))
                .andExpect(status().isUnauthorized());

        verify(jwtTokenProvider).validateToken(testRefreshToken);
    }

    @Test
    @DisplayName("재로그인 레프레시 토큰없음 오류시 처리가 잘 되는지 확인")
    void reSignInFailByNoTokenTest() throws Exception {
        mockMvc.perform(post("/auth/reissue")).andExpect(status().isUnauthorized());

        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("로그아웃 성공 시 잘 처리되는지 확인")
    void signOutTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "user-abc";

        given(jwtTokenProvider.validateToken(testAccessToken)).willReturn(true);

        Authentication mockAuthentication = mock(Authentication.class);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);

        mockMvc.perform(post("/auth/signOut").cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("accessToken="))))
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("refreshToken="))));

        verify(authService).signOut(testUserId);
    }

    @Test
    @DisplayName("로그아웃 토큰 오류 시 잘 처리되는지 확인")
    void signOutFailByNonTokenTest() throws Exception {
        mockMvc.perform(post("/auth/signOut"))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("accessToken="))))
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("refreshToken="))));

        verify(authService, never()).signOut(anyString());
    }

    @Test
    @DisplayName("로그아웃 토큰 오류 시 잘 처리되는지 확인")
    void signOutFailInvalidTokenTest() throws Exception {
        String testAccessToken = "access-123";

        given(jwtTokenProvider.validateToken(testAccessToken)).willReturn(false);

        mockMvc.perform(post("/auth/signOut").cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("accessToken="))))
                .andExpect(
                        header().stringValues(
                                        HttpHeaders.SET_COOKIE,
                                        hasItem(containsString("refreshToken="))));

        verify(authService, never()).signOut(anyString());
    }

    @Test
    @DisplayName("토큰 검증 성공 시 잘 처리되는지 확인")
    void validateTokenTest() throws Exception {
        String testAccessToken = "access-123";

        given(jwtTokenProvider.validateToken(testAccessToken)).willReturn(true);

        mockMvc.perform(post("/auth/validate").cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isOk());

        verify(jwtTokenProvider).validateToken(testAccessToken);
    }

    @Test
    @DisplayName("토큰 검증 실패 시 잘 처리되는지 확인")
    void validateTokenFailTest() throws Exception {
        String testAccessToken = "access-123";

        given(jwtTokenProvider.validateToken(testAccessToken)).willReturn(false);

        mockMvc.perform(post("/auth/validate").cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isUnauthorized());

        verify(jwtTokenProvider).validateToken(testAccessToken);
    }

    @Test
    @DisplayName("토큰 검증 때 토큰이 없을 시 잘 처리되는지 확인")
    void validateTokenFailByNoTokenTest() throws Exception {
        mockMvc.perform(post("/auth/validate")).andExpect(status().isUnauthorized());

        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("토큰 검증 때 토큰이 비었을 시 잘 처리되는지 확인")
    void validateTokenFailByEmptyTokenTest() throws Exception {
        mockMvc.perform(post("/auth/validate").cookie(new Cookie("accessToken", "")))
                .andExpect(status().isUnauthorized());

        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("회원 탈퇴 성공 시 잘 처리되는지 확인")
    void resignUserTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";

        Authentication mockAuthentication = mock(Authentication.class);
        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.resignUser(testUserId)).willReturn(true);

        mockMvc.perform(
                        delete("/auth/resignUser")
                                .cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isOk());

        verify(authService).resignUser(testUserId);
    }

    @Test
    @DisplayName("회원 탈퇴 시도 시 유저가 존재하지 않을 때 잘 처리되는지 확인")
    void resignUserFailTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";

        Authentication mockAuthentication = mock(Authentication.class);
        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.resignUser(testUserId)).willReturn(false);

        mockMvc.perform(
                        delete("/auth/resignUser")
                                .cookie(new Cookie("accessToken", testAccessToken)))
                .andExpect(status().isNotFound());

        verify(authService).resignUser(testUserId);
    }

    @Test
    @DisplayName("비밀번효 변경 성공시 잘 처리되는지 확인")
    void changePasswordTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.changePassword(testUserId, password.getPassword())).willReturn(true);

        mockMvc.perform(
                        patch("/auth/chagePassword")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isOk());

        verify(authService).changePassword(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("비밀번효 변경 시 유저가 존재하지 않을 때 잘 처리되는지 확인")
    void changePasswordFailTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.changePassword(testUserId, password.getPassword())).willReturn(false);

        mockMvc.perform(
                        patch("/auth/chagePassword")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isNotFound());

        verify(authService).changePassword(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("유저 재확인이 성공했을 때 잘 처리되는지 확인")
    void checkUserTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.checkUser(testUserId, password.getPassword())).willReturn(true);

        mockMvc.perform(
                        post("/auth/checkUser")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isOk());

        verify(authService).checkUser(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("유저 재확인이 실패했을 때 잘 처리되는지 확인")
    void checkUserFailTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.checkUser(testUserId, password.getPassword())).willReturn(false);

        mockMvc.perform(
                        post("/auth/checkUser")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isUnauthorized());

        verify(authService).checkUser(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("유저 재확인시 유저가 존재하지 않을 때 때 잘 처리되는지 확인")
    void checkUserFailByNoSuchElementExceptionTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.checkUser(testUserId, password.getPassword()))
                .willThrow(new NoSuchElementException("존재하지 않는 사용자입니다."));

        mockMvc.perform(
                        post("/auth/checkUser")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isNotFound());

        verify(authService).checkUser(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("유저 재확인시 서버 오류가 발생했을 때 잘 처리되는지 확인")
    void checkUserFailByServerExceptionTest() throws Exception {
        String testAccessToken = "access-123";
        String testUserId = "userid1234";
        PasswordDto password = new PasswordDto("password!234");
        Authentication mockAuthentication = mock(Authentication.class);

        given(jwtTokenProvider.getAuthentication(testAccessToken)).willReturn(mockAuthentication);
        given(mockAuthentication.getName()).willReturn(testUserId);
        given(authService.checkUser(testUserId, password.getPassword()))
                .willThrow(new RuntimeException("서버 오류입니다."));

        mockMvc.perform(
                        post("/auth/checkUser")
                                .cookie(new Cookie("accessToken", testAccessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(password)))
                .andExpect(status().isInternalServerError());

        verify(authService).checkUser(testUserId, password.getPassword());
    }

    @Test
    @DisplayName("중복 아이디가 아닐 경우 잘 처리하는지 확인")
    void checkITestd() throws Exception {
        String testId = "userid1234";

        given(authService.idCheck(testId)).willReturn(false);

        mockMvc.perform(get("/auth/checkId").param("id", testId)).andExpect(status().isOk());

        verify(authService).idCheck(testId);
    }

    @Test
    @DisplayName("중복 아이디일 경우 잘 처리하는지 확인")
    void checkDuplicatedIdTest() throws Exception {
        String testId = "userid1234";

        given(authService.idCheck(testId)).willReturn(true);

        mockMvc.perform(get("/auth/checkId").param("id", testId)).andExpect(status().isConflict());

        verify(authService).idCheck(testId);
    }

    @Test
    @DisplayName("아이디가 전달되지 않은 경우 잘 처리하는지 확인")
    void checkNotConveyedIdTest() throws Exception {
        String testId = "userid1234";

        given(authService.idCheck(testId)).willReturn(true);

        mockMvc.perform(get("/auth/checkId")).andExpect(status().isBadRequest());

        verify(authService, never()).idCheck(testId);
    }

    @Test
    @DisplayName("빈 아이디가 전달되는 경우 잘 처리하는지 확인")
    void checkEmptyIdTest() throws Exception {
        String testId = "";

        given(authService.idCheck(testId)).willReturn(true);

        mockMvc.perform(get("/auth/checkId").param("id", testId))
                .andExpect(status().isBadRequest());

        verify(authService, never()).idCheck(testId);
    }

    @Test
    @DisplayName("중복 메일가 아닐 경우 잘 처리하는지 확인")
    void checkMailTest() throws Exception {
        String testMail = "testmail@gmail.com";

        given(authService.emailCheck(testMail)).willReturn(false);

        mockMvc.perform(get("/auth/checkEmail").param("email", testMail))
                .andExpect(status().isOk());

        verify(authService).emailCheck(testMail);
    }

    @Test
    @DisplayName("중복 메일일 경우 잘 처리하는지 확인")
    void checkDuplicatedMailTest() throws Exception {
        String testMail = "testmail@gmail.com";

        given(authService.emailCheck(testMail)).willReturn(true);

        mockMvc.perform(get("/auth/checkEmail").param("email", testMail))
                .andExpect(status().isConflict());

        verify(authService).emailCheck(testMail);
    }

    @Test
    @DisplayName("메일이 전달되지 않은 경우 잘 처리하는지 확인")
    void checkNotConveyedMailTest() throws Exception {
        String testMail = "testmail@gmail.com";

        given(authService.emailCheck(testMail)).willReturn(true);

        mockMvc.perform(get("/auth/checkEmail")).andExpect(status().isBadRequest());

        verify(authService, never()).emailCheck(testMail);
    }

    @Test
    @DisplayName("빈 메일이 전달되는 경우 잘 처리하는지 확인")
    void checkEmptyMailTest() throws Exception {
        String testMail = "";

        given(authService.emailCheck(testMail)).willReturn(true);

        mockMvc.perform(get("/auth/checkEmail").param("email", testMail))
                .andExpect(status().isBadRequest());

        verify(authService, never()).emailCheck(testMail);
    }
}
