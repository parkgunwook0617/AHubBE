package ahubbe.ahubbe.repository;

import ahubbe.ahubbe.entity.AnimationInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AnimationInformation, Long> {
    Optional<AnimationInformation> findByTitle(String title);
    void deleteByTitle(String title);
}
