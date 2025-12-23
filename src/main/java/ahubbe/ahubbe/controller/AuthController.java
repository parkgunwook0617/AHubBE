package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping(path = "/register")
    public void signin(String id, String password) {
        authService.registerUser(id, password);
    }
}
