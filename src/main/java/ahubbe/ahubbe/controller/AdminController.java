package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AdminRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AdminRepository adminRepository;

    @GetMapping(path = "/inquiry")
    public List<AnimationInformation> inquiry(String year, String quarter) {
        return adminService.saveAnimeData(year, quarter, "title");
    }

    @GetMapping(path = "/selfInquiry")
    public AnimationInformation selfInquiry(
            @ModelAttribute AnimeDto animeDto, String year, String quarter) {
        return adminService.saveSingleAnimeData(animeDto, year, quarter);
    }
}
