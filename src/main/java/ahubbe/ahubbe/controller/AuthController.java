package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.*;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto requestDto) {
        authService.registerUser(
                requestDto.getId(), requestDto.getPassword(), requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping(path = "/signIn")
    public ResponseEntity<?> signin(@RequestBody AuthDto requestDto, HttpServletResponse response) {
        try {
            JwtToken jwtToken = authService.signIn(requestDto.getId(), requestDto.getPassword());

            ResponseCookie cookie =
                    ResponseCookie.from("accessToken", jwtToken.getAccessToken())
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("None")
                            .path("/")
                            .maxAge(60 * 60)
                            .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok("로그인 성공");
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "LOGIN_FAIL", "message", "아이디 또는 비밀번호가 틀렸습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping(path = "/signOut")
    public ResponseEntity<?> signout(HttpServletResponse response) {
        ResponseCookie cookie =
                ResponseCookie.from("accessToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .path("/")
                        .maxAge(0)
                        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @CookieValue(name = "accessToken", required = false) @RequestBody TokenDto tokenDto) {
        String token = tokenDto.getToken();

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 존재하지 않습니다.");
        }

        boolean isValid = jwtTokenProvider.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않거나 만료된 토큰입니다.");
        }
    }

    @DeleteMapping("/resignUser")
    public ResponseEntity<?> resignUser(@CookieValue(name = "accessToken") String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();

        boolean response = authService.resignUser(userId);
        if (response) {
            return ResponseEntity.ok("정상적으로 탈퇴 처리되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @PatchMapping("/chagePassword")
    public ResponseEntity<?> changePassword(
            @CookieValue(name = "accessToken") String token, @RequestBody PasswordDto passwordDto) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();
        String newPassword = passwordDto.getPassword();

        boolean response = authService.changePassword(userId, newPassword);

        if (response) {
            return ResponseEntity.ok("비밀번호 변경 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/checkUser")
    public ResponseEntity<?> checkUser(
            @CookieValue(name = "accessToken") String token, @RequestBody PasswordDto passwordDto) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();
        String password = passwordDto.getPassword();

        try {
            boolean isMatched = authService.checkUser(userId, password);

            if (isMatched) {
                return ResponseEntity.ok("인증에 성공하였습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 사용자입니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("인증 처리 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/checkId")
    public ResponseEntity<?> checkId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("아이디를 입력해주세요.");
        }

        if (authService.idCheck(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
        } else {
            return ResponseEntity.ok().body("사용 가능한 아이디입니다.");
        }
    }

    @GetMapping("/checkEmail")
    public ResponseEntity<?> checkEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력해주세요.");
        }

        if (authService.emailCheck(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다.");
        } else {
            return ResponseEntity.ok().body("사용 가능한 이메일입니다.");
        }
    }
}
