package ahubbe.ahubbe.service.User;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AnimationRepository animationRepository;

    public List<AnimationInformation> importAnimationInformation() {

        return animationRepository.findAll();
    }

    public List<AnimationInformation> importAnimationInformationByTitle(String Title) {

        return animationRepository.findAllByTitleContaining(Title);
    }

    public List<AnimationInformation> importAnimationInformationByGenre(String Genre) {

        return animationRepository.findAllByGenreListContaining(Genre);
    }

    public List<AnimationInformation> importAnimationInformationByReleaseYear(String ReleaseYear) {

        return animationRepository.findAllByReleaseYearContaining(ReleaseYear);
    }

    public List<AnimationInformation> importAnimationInformationByReleaseQuarter(
            String ReleaseQuarter) {

        return animationRepository.findAllByReleaseQuarterContaining(ReleaseQuarter);
    }
}
