package ahubbe.ahubbe.Integration;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminServiceIntegrationTest {

    @Autowired AdminService adminService;

    @Autowired AnimationRepository animationRepository;

    @Value("classpath:payload/sampleData.json")
    private Resource sampleJson;

    @Test
    @DisplayName("수동 저장이 DB에 잘 저장되는지 확인")
    void selfInquiryTest() {
        AnimeDto animeDto = new AnimeDto("testTitle", "testKeyVisual", List.of("test"));
        String testYear = "2025";
        String testQuarter = "1";
        adminService.saveSingleAnimeData(animeDto, testYear, testQuarter);

        Assertions.assertThat(animationRepository.findByTitle("testTitle")).isPresent();
        Assertions.assertThat(animationRepository.findByTitle("testTitle").get().getReleaseYear())
                .containsOnly(2025);
        Assertions.assertThat(
                        animationRepository.findByTitle("testTitle").get().getReleaseQuarter())
                .containsOnly(1);
    }

    @Test
    @DisplayName("수동 저장에서 한 애니메이션의 분기가 여러개일 때, DB에 잘 저장되는지 확인")
    void selfInquiryMultipleTest() {
        AnimeDto animeDto = new AnimeDto("testTitle", "testKeyVisual", List.of("test"));
        String testYear = "2025";
        String testQuarter = "1";
        String testQuarter2 = "2";
        adminService.saveSingleAnimeData(animeDto, testYear, testQuarter);
        adminService.saveSingleAnimeData(animeDto, testYear, testQuarter2);

        Assertions.assertThat(animationRepository.findByTitle("testTitle")).isPresent();
        Assertions.assertThat(animationRepository.findByTitle("testTitle").get().getReleaseYear())
                .containsOnly(2025);
        Assertions.assertThat(
                        animationRepository.findByTitle("testTitle").get().getReleaseQuarter())
                .contains(1, 2);
    }

    @Test
    @DisplayName("JSON 파일을 업로드하여 애니메이션 데이터를 저장/업데이트되는지 확인")
    void saveAnimeDataTest() throws IOException {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", sampleJson.getInputStream());

        List<AnimationInformation> savedList = adminService.saveAnimeData(file, "2025", "2");

        Assertions.assertThat(savedList).isNotEmpty();
        Assertions.assertThat(savedList).hasSize(81);

        String firstTitle = savedList.get(0).getTitle();
        AnimationInformation found = animationRepository.findByTitle(firstTitle).orElseThrow();

        Assertions.assertThat(found.getReleaseYear()).contains(2025);
        Assertions.assertThat(found.getReleaseQuarter()).contains(2);
    }

    @Test
    @DisplayName("동일한 애니메이션을 다른 분기에 저장하면 분기가 누적되는지 학인")
    void saveAnimeDataDuplicateQuarterAccumulationTest() throws IOException {
        MockMultipartFile firstFile =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", sampleJson.getInputStream());
        adminService.saveAnimeData(firstFile, "2025", "1");

        MockMultipartFile secondFile =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", sampleJson.getInputStream());
        List<AnimationInformation> savedList = adminService.saveAnimeData(secondFile, "2025", "2");

        Assertions.assertThat(savedList).isNotEmpty();

        String firstTitle = savedList.get(0).getTitle();
        AnimationInformation found = animationRepository.findByTitle(firstTitle).orElseThrow();

        Assertions.assertThat(found.getReleaseYear()).contains(2025);
        Assertions.assertThat(found.getReleaseQuarter()).hasSize(2).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("동일한 애니메이션을 다른 연도에 저장하면 연도가 누적되는지 학인")
    void saveAnimeDataDuplicateYearAccumulationTest() throws IOException {
        MockMultipartFile firstFile =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", sampleJson.getInputStream());
        adminService.saveAnimeData(firstFile, "2025", "2");

        MockMultipartFile secondFile =
                new MockMultipartFile(
                        "file", "sampleData.json", "application/json", sampleJson.getInputStream());
        List<AnimationInformation> savedList = adminService.saveAnimeData(secondFile, "2026", "2");

        Assertions.assertThat(savedList).isNotEmpty();

        String firstTitle = savedList.get(0).getTitle();
        AnimationInformation found = animationRepository.findByTitle(firstTitle).orElseThrow();

        Assertions.assertThat(found.getReleaseYear())
                .hasSize(2)
                .containsExactlyInAnyOrder(2025, 2026);
        Assertions.assertThat(found.getReleaseQuarter()).contains(2);
    }
}
