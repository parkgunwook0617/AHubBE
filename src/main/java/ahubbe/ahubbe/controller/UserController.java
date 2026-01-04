package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.User.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AnimationRepository animationRepository;
    private final UserService userService;

    @GetMapping("/findAll")
    public Iterable<AnimationInformation> findAll() {
        return userService.importAnimationInformation();
    }

    @GetMapping("/findAllByTitle")
    public Iterable<AnimationInformation> findAllByTitle(String title) {
        return userService.importAllAnimationInformationByTitle(title);
    }

    @GetMapping("/findAllByGenre")
    public Iterable<AnimationInformation> findAllByGenre(String genre) {
        return userService.importAnimationInformationByGenre(genre);
    }

    @GetMapping("/findAllByReleaseYear")
    public Iterable<AnimationInformation> findAllByReleaseYear(String releaseYear) {
        return userService.importAnimationInformationByReleaseYear(releaseYear);
    }

    @GetMapping("/findAllByReleaseQuarter")
    public Iterable<AnimationInformation> findAllByReleaseQuarter(String releaseQuarter) {
        return userService.importAnimationInformationByReleaseQuarter(releaseQuarter);
    }

    @GetMapping("/findSingleAnimation")
    public Optional<AnimationInformation> findSingleAnimation(String title) {
        return userService.importSingleAnimationInformationByTitle(title);
    }

    @PostMapping("/saveFavorite")
    public ResponseEntity<?> saveFavoriteAnimationInformation(
            @CookieValue(name = "accessToken") String token, String title) {
        userService.addFavorite(token, title);

        return ResponseEntity.ok("성공적으로 저장되었습니다.");
    }

    @GetMapping("/getFavorite")
    public List<AnimationInformation> getFavoriteAnimationInformation(
            @CookieValue(name = "accessToken") String token) {
        return userService.getFavorite(token);
    }

    @GetMapping("/getGenreList")
    public List<String> getGenreList() {
        return userService.getGenreList();
    }
}
