package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public JwtToken signin(String id, String password) {
        return authService.signIn(id, password);
    }
}
