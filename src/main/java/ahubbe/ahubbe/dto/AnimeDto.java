package ahubbe.ahubbe.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDto {
    @NotBlank(message = "애니메이션 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "키 비주얼 이미지는 필수입니다.")
    private String keyVisual;

    private List<String> genreList;
}
