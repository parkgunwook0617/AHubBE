package ahubbe.ahubbe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    private String email;
    private String authCode;
    private LocalDateTime createdDate;

    private String token;
    private String tempPassword;

    public Auth(String email, String authCode) {
        this.email = email;
        this.authCode = authCode;
        this.createdDate = LocalDateTime.now();
    }

    public Auth(String email, String token, String tempPassword) {
        this.email = email;
        this.token = token;
        this.tempPassword = tempPassword;
        this.createdDate = LocalDateTime.now();
    }
}
