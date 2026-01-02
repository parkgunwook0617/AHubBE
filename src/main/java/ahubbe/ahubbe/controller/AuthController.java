package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.service.Auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity register(String id, String password) {
        authService.registerUser(id, password);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @GetMapping(path = "/signIn")
    public ResponseEntity<String> signin(String id, String password, HttpServletResponse response) {
        JwtToken jwtToken = authService.signIn(id, password);

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
    }
}
