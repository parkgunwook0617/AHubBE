package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AnimationRepository;
import ahubbe.ahubbe.service.Admin.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AnimationRepository animationRepository;

    @GetMapping(path = "/inquiry")
    public List<AnimeDto> inquiry(String year, String quarter) {
        return adminService.getAnimeDetailElements(year, quarter, "title");
    }

    @PostMapping(path = "/selfInquiry")
    public AnimationInformation selfInquiry(@Valid AnimeDto animeDto, String year, String quarter) {
        return adminService.saveSingleAnimeData(animeDto, year, quarter);
    }

    @PostMapping(path = "/animeDataUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AnimationInformation>> uploadJSON(
            @RequestPart("file") MultipartFile file, String year, String quarter) {
        List<AnimationInformation> result = adminService.saveAnimeData(file, year, quarter);
        return ResponseEntity.ok(result);
    }
}
