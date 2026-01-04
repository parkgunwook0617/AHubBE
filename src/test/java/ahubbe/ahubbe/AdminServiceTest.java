package ahubbe.ahubbe;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AdminServiceTest {

    @Autowired AdminService adminService;

    @Autowired AnimationRepository animationRepository;

    @Test
    @DisplayName("수동 저장이 DB에 잘 저장되는지 확인")
    void selfInquiryTest() {
        AnimeDto animeDto = new AnimeDto("testTitle", "testKeyVisual", List.of("test"));
        String testYear = "2025";
        String testQuarter = "1";
        adminService.saveSingleAnimeData(animeDto, testYear, testQuarter);
    }
}
