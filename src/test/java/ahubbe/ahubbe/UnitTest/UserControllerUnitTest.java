package ahubbe.ahubbe.UnitTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import ahubbe.ahubbe.controller.UserController;
import ahubbe.ahubbe.dto.TitleDto;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.User.UserService;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

public class UserControllerUnitTest {
    private MockMvc mockMvc;
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        AnimationRepository animationRepository = mock(AnimationRepository.class);
        UserController userController = new UserController(animationRepository, userService);

        mockMvc = standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("전체 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryTest() throws Exception {
        given(userService.importAnimationInformation()).willReturn(List.of());

        mockMvc.perform(get("/user/findAll")).andExpect(status().isOk());

        verify(userService).importAnimationInformation();
    }

    @Test
    @DisplayName("제목으로 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryTitleTest() throws Exception {
        given(userService.importAllAnimationInformationByTitle(anyString())).willReturn(List.of());

        mockMvc.perform(get("/user/findAllByTitle").param("title", "testTitle"))
                .andExpect(status().isOk());

        verify(userService).importAllAnimationInformationByTitle("testTitle");
    }

    @Test
    @DisplayName("장르로 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryGenreTest() throws Exception {
        given(userService.importAnimationInformationByGenre(anyString())).willReturn(List.of());

        mockMvc.perform(get("/user/findAllByGenre").param("genre", "testGenre"))
                .andExpect(status().isOk());

        verify(userService).importAnimationInformationByGenre("testGenre");
    }

    @Test
    @DisplayName("상영년도로 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryReleaseYearTest() throws Exception {
        given(userService.importAnimationInformationByReleaseYear(anyString()))
                .willReturn(List.of());

        mockMvc.perform(get("/user/findAllByReleaseYear").param("releaseYear", "testReleaseYear"))
                .andExpect(status().isOk());

        verify(userService).importAnimationInformationByReleaseYear("testReleaseYear");
    }

    @Test
    @DisplayName("상영분기로 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryReleaseQuarterTest() throws Exception {
        given(userService.importAnimationInformationByReleaseYear(anyString()))
                .willReturn(List.of());

        mockMvc.perform(
                        get("/user/findAllByReleaseQuarter")
                                .param("releaseQuarter", "testReleaseQuarter"))
                .andExpect(status().isOk());

        verify(userService).importAnimationInformationByReleaseQuarter("testReleaseQuarter");
    }

    @Test
    @DisplayName("단일 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquirySingleTest() throws Exception {
        given(userService.importAnimationInformationByReleaseYear(anyString()))
                .willReturn(List.of());

        mockMvc.perform(get("/user/findSingleAnimation").param("title", "testTitle"))
                .andExpect(status().isOk());

        verify(userService).importSingleAnimationInformationByTitle("testTitle");
    }

    @Test
    @DisplayName("선호 애니메이션 등록 시 서비스 메서드가 호출되는 것 확인")
    void saveFavoriteTest() throws Exception {
        TitleDto titleDto = new TitleDto("testTitle");
        String token = "valid-access-token";

        mockMvc.perform(
                        post("/user/saveFavorite")
                                .cookie(new Cookie("accessToken", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(titleDto)))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            String responseBody =
                                    result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                            Assertions.assertThat(responseBody).isNotNull();
                        });

        verify(userService).addFavorite(token, titleDto.getTitle());
    }

    @Test
    @DisplayName("선호 애니메이션 삭제 시 서비스 메서드가 호출되는 것 확인")
    void deleteFavoriteTest() throws Exception {
        String token = "valid-access-token";

        mockMvc.perform(
                        delete("/user/deleteFavorite")
                                .cookie(new Cookie("accessToken", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .param("title", "testTitle"))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            String responseBody =
                                    result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                            Assertions.assertThat(responseBody).isNotNull();
                        });

        verify(userService).removeFavorite(token, "testTitle");
    }

    @Test
    @DisplayName("선호 애니메이션 목록 요청 시 서비스 메서드가 호출되는 것 확인")
    void getFavoriteTest() throws Exception {
        String token = "valid-access-token";

        mockMvc.perform(get("/user/getFavorite").cookie(new Cookie("accessToken", token)))
                .andExpect(status().isOk());

        verify(userService).getFavorite(token);
    }

    @Test
    @DisplayName("장르 목록 요청 시 서비스 메서드가 호출되는 것 확인")
    void getGenreTest() throws Exception {
        mockMvc.perform(get("/user/getGenreList")).andExpect(status().isOk());

        verify(userService).getGenreList();
    }
}
