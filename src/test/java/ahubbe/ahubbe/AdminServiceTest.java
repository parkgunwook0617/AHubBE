package ahubbe.ahubbe;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AdminRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class AdminServiceTest {

    @Autowired
    AdminService adminService;

    @Autowired
    AdminRepository adminRepository;


    @Test
    @DisplayName("데이터 뭉치가 DB에 잘 저장되는지 확인")
    void inquiryTest() {
        List<AnimationInformation> Elements = adminService.saveAnimeData("2025", "1", "title");

        long savedRight = 0;

        for(AnimationInformation animationInformation : Elements){
            if(adminRepository.findByTitle(animationInformation.getTitle()).isPresent()) {
                savedRight++;
            }
        }

        Assertions.assertThat(savedRight).isEqualTo(Elements.size());
    }

    @Test
    @DisplayName("수동 저장이 DB에 잘 저장되는지 확인")
    void selfInquiryTest() {
        AnimeDto animeDto = new AnimeDto("testTitle", "testKeyVisual", List.of("test"));
        String testYear = "2025";
        String testQuarter = "1";
        adminService.saveSingleAnimeData(animeDto, testYear, testQuarter);
    }
}
