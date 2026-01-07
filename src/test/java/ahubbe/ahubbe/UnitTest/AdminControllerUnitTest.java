package ahubbe.ahubbe.UnitTest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import ahubbe.ahubbe.controller.AdminController;
import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

public class AdminControllerUnitTest {
    private MockMvc mockMvc;
    private AdminService adminService;
    private AnimationRepository animationRepository;

    @BeforeEach
    void setUp() {
        adminService = mock(AdminService.class);
        animationRepository = mock(AnimationRepository.class);
        AdminController adminController = new AdminController(adminService, animationRepository);

        mockMvc = standaloneSetup(adminController).build();
    }

    @Test
    @DisplayName("연도와 분기로 애니메이션 조회 시 서비스 메서드가 호출되는 것 확인")
    void inquiryTest() throws Exception {
        given(adminService.getAnimeDetailElements(anyString(), anyString(), anyString()))
                .willReturn(List.of());

        mockMvc.perform(get("/admin/inquiry").param("year", "2025").param("quarter", "1"))
                .andExpect(status().isOk());

        verify(adminService).getAnimeDetailElements("2025", "1", "title");
    }

    @Test
    @DisplayName("DTO와 연도, 분기를 전달하면 단일 애니메이션 데이터가 저장되는 것 확인")
    void selfInquiryTest() throws Exception {

        AnimationInformation mockInfo = new AnimationInformation();
        given(adminService.saveSingleAnimeData(any(), anyString(), anyString()))
                .willReturn(mockInfo);

        mockMvc.perform(
                        post("/admin/selfInquiry")
                                .param("title", "테스트제목")
                                .param("keyVisual", "이미지경로")
                                .param("year", "2025")
                                .param("quarter", "1"))
                .andExpect(status().isOk());

        verify(adminService).saveSingleAnimeData(any(AnimeDto.class), eq("2025"), eq("1"));
    }

    @Test
    @DisplayName("JSON 파일 업로드 시 애니메이션 정보 리스트가 저장 및 반환되는 것 확인")
    void uploadJSONTest() throws Exception {
        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "test.json", "application/json", "{\"title\":\"test\"}".getBytes());

        List<AnimationInformation> mockResult = List.of(new AnimationInformation());
        given(adminService.saveAnimeData(any(), anyString(), anyString())).willReturn(mockResult);

        mockMvc.perform(
                        multipart("/admin/animeDataUpload")
                                .file(mockFile)
                                .param("year", "2025")
                                .param("quarter", "1"))
                .andExpect(status().isOk());

        verify(adminService).saveAnimeData(any(), eq("2025"), eq("1"));
    }
}
