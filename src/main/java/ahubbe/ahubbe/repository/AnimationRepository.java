package ahubbe.ahubbe.repository;

import ahubbe.ahubbe.entity.AnimationInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimationRepository extends JpaRepository<AnimationInformation, Long> {
    Optional<AnimationInformation> findByTitle(String title);

    List<AnimationInformation> findAllByTitleContaining(String title);

    List<AnimationInformation> findAllByGenreListContaining(String genre);

    List<AnimationInformation> findAllByReleaseYearContaining(String releaseYear);

    List<AnimationInformation> findAllByReleaseQuarterContaining(String releaseQuarter);

    void deleteByTitle(String title);
}
