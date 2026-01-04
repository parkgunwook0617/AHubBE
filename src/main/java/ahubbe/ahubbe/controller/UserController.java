package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AnimationRepository animationRepository;

    @GetMapping("/findAll")
    public Iterable<AnimationInformation> findAll() {
        return animationRepository.findAll();
    }

    @GetMapping("/findAllByTitle")
    public Iterable<AnimationInformation> findAllByTitle(String title) {
        return animationRepository.findAllByTitleContaining(title);
    }

    @GetMapping("/findAllByGenre")
    public Iterable<AnimationInformation> findAllByGenre(String genre) {
        return animationRepository.findAllByGenreListContaining(genre);
    }

    @GetMapping("/findAllByReleaseYear")
    public Iterable<AnimationInformation> findAllByReleaseYear(String releaseYear) {
        return animationRepository.findAllByReleaseYearContaining(releaseYear);
    }

    @GetMapping("/findAllByReleaseQuarter")
    public Iterable<AnimationInformation> findAllByReleaseQuarter(String releaseQuarter) {
        return animationRepository.findAllByReleaseQuarterContaining(releaseQuarter);
    }
}
