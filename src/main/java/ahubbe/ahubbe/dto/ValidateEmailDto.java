package ahubbe.ahubbe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateEmailDto {
    String email;
    String authCode;
}
