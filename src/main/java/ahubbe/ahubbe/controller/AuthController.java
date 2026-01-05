package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.AuthDto;
import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(path = "/register")
    public ResponseEntity register(@RequestBody AuthDto requestDto) {
        authService.registerUser(requestDto.getId(), requestDto.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping(path = "/signIn")
    public ResponseEntity<String> signin(
            @RequestBody AuthDto requestDto, HttpServletResponse response) {
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
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @CookieValue(name = "accessToken", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isValid = jwtTokenProvider.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/resignUser")
    public ResponseEntity<?> resignUser(@CookieValue(name = "accessToken") String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();

        boolean response = authService.resignUser(userId);
        if (response) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/chagePassword")
    public ResponseEntity<?> changePassword(
            @CookieValue(name = "accessToken") String token, String newPassword) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();

        boolean response = authService.changePassword(userId, newPassword);

        if (response) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/checkUser")
    public ResponseEntity<?> checkUser(
            @CookieValue(name = "accessToken") String token, String password) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        String userId = authentication.getName();

        boolean response = authService.checkUser(userId, password);

        if (response) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
