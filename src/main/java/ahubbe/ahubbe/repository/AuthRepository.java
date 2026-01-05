package ahubbe.ahubbe.repository;

import ahubbe.ahubbe.entity.Auth;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, String> {
    Auth findByEmail(String email);

    Auth findByToken(String token);

    void deleteByEmail(String email);

    void deleteByCreatedDateBefore(LocalDateTime date);
}
