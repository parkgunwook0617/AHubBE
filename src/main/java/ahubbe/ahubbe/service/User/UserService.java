package ahubbe.ahubbe.service.User;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.repository.UserRepository;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
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

    public List<AnimationInformation> importAllAnimationInformationByTitle(String title) {

        return animationRepository.findAllByTitleContaining(title);
    }

    public List<AnimationInformation> importAnimationInformationByGenre(String genre) {

        return animationRepository.findAllByGenreListContaining(genre);
    }

    public List<AnimationInformation> importAnimationInformationByReleaseYear(String releaseYear) {

        return animationRepository.findAllByReleaseYearContaining(releaseYear);
    }

    public List<AnimationInformation> importAnimationInformationByReleaseQuarter(
            String releaseQuarter) {

        return animationRepository.findAllByReleaseQuarterContaining(releaseQuarter);
    }

    public Optional<AnimationInformation> importSingleAnimationInformationByTitle(String title) {

        return animationRepository.findByTitle(title);
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

        user.getFavoriteAnimations()
                .add(
                        animationRepository
                                .findByTitle(title)
                                .orElseThrow(
                                        () -> new EntityNotFoundException("애니메이션 정보를 찾을 수 없습니다.")));
    }

    @Transactional
    public void removeFavorite(String token, String title) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.getFavoriteAnimations()
                .remove(
                        animationRepository
                                .findByTitle(title)
                                .orElseThrow(
                                        () -> new EntityNotFoundException("애니메이션 정보를 찾을 수 없습니다.")));
    }

    public Set<AnimationInformation> getFavorite(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getFavoriteAnimations();
    }

    public ArrayList<String> getGenreList() {
        List<AnimationInformation> list = animationRepository.findAll();
        Set<String> genreList = new HashSet<>();

        for (AnimationInformation animationInformation : list) {
            genreList.addAll(animationInformation.getGenreList());
        }

        return new ArrayList<String>(genreList);
    }
}
