package ahubbe.ahubbe.repository;

import ahubbe.ahubbe.entity.AnimationInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AnimationInformation, Long> {
    Optional<AnimationInformation> findByTitle(String title);

    List<AnimationInformation> findAllByTitleContaining(String title);

    void deleteByTitle(String title);
}
