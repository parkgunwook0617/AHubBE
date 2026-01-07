package ahubbe.ahubbe.Integration;

import ahubbe.ahubbe.dto.JwtToken;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.service.Admin.AdminService;
import ahubbe.ahubbe.service.Auth.AuthService;
import ahubbe.ahubbe.service.Auth.JwtTokenProvider;
import ahubbe.ahubbe.service.User.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired UserService userService;
    @Autowired AdminService adminService;
    @Autowired AuthService authService;
    @Autowired JwtTokenProvider jwtTokenProvider;

    private String extractedToken;

    @BeforeEach
    void setUp() throws IOException {
        authService.registerUser("testUser", "testPassword", "test@gmail.com");

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "testUser", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        JwtToken jwtToken = jwtTokenProvider.generateToken(auth);
        this.extractedToken = jwtToken.getAccessToken();

        ClassPathResource resource = new ClassPathResource("payload/sampleData.json");

        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", resource.getInputStream());

        adminService.saveAnimeData(file, "2025", "2");
    }

    @Test
    @DisplayName("데이터 전조회가 잘 되는지 확인")
    void allDataQueryTest() {
        Assertions.assertThat(userService.importAnimationInformation().size()).isEqualTo(81);
    }

    @Test
    @DisplayName("제목 조회가 잘 되는지 확인")
    void specificTitleDataQueryTest() {
        Assertions.assertThat(userService.importAllAnimationInformationByTitle("판").size())
                .isEqualTo(2);
        Assertions.assertThat(userService.importAllAnimationInformationByTitle("s").size())
                .isEqualTo(8);
        Assertions.assertThat(userService.importAllAnimationInformationByTitle("가").size())
                .isEqualTo(15);
    }

    @Test
    @DisplayName("장르 조회가 잘 되는지 확인")
    void specificGenreQueryTest() {
        Assertions.assertThat(userService.importAnimationInformationByGenre("판타지").size())
                .isEqualTo(16);
        Assertions.assertThat(userService.importAnimationInformationByGenre("하렘").size())
                .isEqualTo(4);
        Assertions.assertThat(userService.importAnimationInformationByGenre("일상").size())
                .isEqualTo(14);
    }

    @Test
    @DisplayName("연도 조회가 잘 되는지 확인")
    void specificReleaseYearQueryTest() {
        Assertions.assertThat(userService.importAnimationInformationByReleaseYear("2025").size())
                .isEqualTo(81);
        Assertions.assertThat(userService.importAnimationInformationByReleaseYear("2024").size())
                .isEqualTo(0);
    }

    @Test
    @DisplayName("분기 조회가 잘 되는지 확인")
    void specificReleaseQuarterQueryTest() {
        Assertions.assertThat(userService.importAnimationInformationByReleaseQuarter("2").size())
                .isEqualTo(81);
        Assertions.assertThat(userService.importAnimationInformationByReleaseQuarter("1").size())
                .isEqualTo(0);
        Assertions.assertThat(userService.importAnimationInformationByReleaseQuarter("3").size())
                .isEqualTo(0);
        Assertions.assertThat(userService.importAnimationInformationByReleaseQuarter("4").size())
                .isEqualTo(0);
    }

    @Test
    @DisplayName("단일 제목 조회가 잘 되는지 확인")
    void singleSpecificTitleQueryTest() {
        AnimationInformation targetData =
                userService
                        .importSingleAnimationInformationByTitle("Summer Pockets/애니메이션")
                        .orElseThrow(() -> new RuntimeException("데이터를 찾을 수 없습니다!"));

        Assertions.assertThat(targetData.getTitle()).isEqualTo("Summer Pockets/애니메이션");
        Assertions.assertThat(targetData.getReleaseYear()).containsOnly(2025);
        Assertions.assertThat(targetData.getReleaseQuarter()).containsOnly(2);
    }

    @Test
    @DisplayName("유저에게 애니메이션 정보가 잘 저장되는지 확인")
    void checkFavoriteAnimationSaveTest() {
        userService.addFavorite(extractedToken, "기동전사 건담 지쿠악스");
        userService.addFavorite(extractedToken, "시운지 가의 아이들/애니메이션");

        Set<AnimationInformation> data = userService.getFavorite(extractedToken);

        Assertions.assertThat(data)
                .hasSize(2)
                .extracting(AnimationInformation::getTitle)
                .contains("기동전사 건담 지쿠악스", "시운지 가의 아이들/애니메이션");

        Assertions.assertThat(data)
                .hasSize(2)
                .extracting(AnimationInformation::getReleaseYear)
                .flatMap(yearList -> yearList)
                .containsExactly(2025, 2025);

        Assertions.assertThat(data)
                .hasSize(2)
                .extracting(AnimationInformation::getReleaseQuarter)
                .flatMap(yearList -> yearList)
                .containsExactly(2, 2);
    }

    @Test
    @DisplayName("유저에게 애니메이션 정보가 잘 제거되는지 확인")
    void checkFavoriteAnimationRemoveTest() {
        userService.addFavorite(extractedToken, "기동전사 건담 지쿠악스");

        Set<AnimationInformation> data = userService.getFavorite(extractedToken);

        Assertions.assertThat(data)
                .hasSize(1)
                .extracting(AnimationInformation::getTitle)
                .containsOnly("기동전사 건담 지쿠악스");

        Assertions.assertThat(data)
                .hasSize(1)
                .extracting(AnimationInformation::getReleaseYear)
                .flatMap(yearList -> yearList)
                .containsExactly(2025);

        Assertions.assertThat(data)
                .hasSize(1)
                .extracting(AnimationInformation::getReleaseQuarter)
                .flatMap(yearList -> yearList)
                .containsExactly(2);

        userService.removeFavorite(extractedToken, "기동전사 건담 지쿠악스");

        Assertions.assertThat(userService.getFavorite(extractedToken).size()).isEqualTo(0);
    }

    @Test
    @DisplayName("없는 유저의 선호 등록 및 삭제가 잘 방지되는지 확인")
    void checkNonExistUserAnimatioNSaveAndAddTest() {
        Assertions.assertThatThrownBy(() -> userService.getFavorite("wrong"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("유효하지 않은 토큰입니다.");

        Assertions.assertThatThrownBy(() -> userService.removeFavorite("wrong", "title"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("유효하지 않은 토큰입니다.");
    }

    @Test
    @DisplayName("없는 유저의 선호 등록 및 삭제가 잘 방지되는지 확인")
    void checkUserRemoveNonExistAnimatioNSaveAndAddTest() {
        Assertions.assertThatThrownBy(() -> userService.removeFavorite(extractedToken, "title"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("애니메이션 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("장르 목록 조회가 잘 되는지 확인")
    void GenreListQueryTest() {
        Assertions.assertThat(userService.getGenreList().size()).isEqualTo(85);
    }
}
