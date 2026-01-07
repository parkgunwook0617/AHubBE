package ahubbe.ahubbe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TitleDto {
    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    String title;
}
