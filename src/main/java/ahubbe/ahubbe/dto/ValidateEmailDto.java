package ahubbe.ahubbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateEmailDto {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    String email;

    String authCode;
}
