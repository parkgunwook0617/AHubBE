package ahubbe.ahubbe.repository;

import ahubbe.ahubbe.entity.AnimationInformation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AnimationInformation, Long> {
    Optional<AnimationInformation> findByTitle(String title);

    void deleteByTitle(String title);
}
