package ahubbe.ahubbe.service.User;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.repository.UserRepository;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AnimationRepository animationRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public List<AnimationInformation> importAnimationInformation() {

        return animationRepository.findAll();
    }

    public List<AnimationInformation> importAllAnimationInformationByTitle(String Title) {

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

    public Optional<AnimationInformation> importSingleAnimationInformationByTitle(String Title) {

        return animationRepository.findByTitle(Title);
    }

    @Transactional
    public void addFavorite(String token, String title) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.getFavoriteAnimations().add(animationRepository.findByTitle(title).get());
    }

    public List<AnimationInformation> getFavorite(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return user.getFavoriteAnimations();
    }

    public ArrayList<String> getGenreList() {
        List<AnimationInformation> list = animationRepository.findAll();
        Set genreList = new HashSet();
        for (AnimationInformation animationInformation : list) {
            genreList.addAll(animationInformation.getGenreList());
        }

        return new ArrayList<String>(genreList);
    }
}
