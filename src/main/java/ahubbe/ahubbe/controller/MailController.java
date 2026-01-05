package ahubbe.ahubbe.controller;

import ahubbe.ahubbe.service.Mail.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @GetMapping("/{email:.+}")
    public ResponseEntity<?> requestAuthcode(@PathVariable("email") String email)
            throws MessagingException {
        boolean isSend = mailService.sendSimpleMessage(email);
        return isSend
                ? ResponseEntity.status(HttpStatus.OK).body("인증 코드가 전송되었습니다.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드 발급에 실패하였습니다.");
    }

    @PostMapping("validatemail")
    public ResponseEntity<?> validEmail(String email, String authCode) {
        boolean isValid = mailService.validateCode(email, authCode);

        return isValid
                ? ResponseEntity.status(HttpStatus.OK).body("인증이 완료되었습니다.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 틀렸습니다.");
    }

    @GetMapping("reset-execute")
    public String executeReset(String token) {
        boolean isSuccess = mailService.verifyTokenAndResetPassword(token);

        if (isSuccess) {
            return "<h1>비밀번호 변경 완료</h1><p>로그인 페이지로 돌아가 임시 비밀번호로 로그인하세요.</p>";
        } else {
            return "<h1>유효하지 않은 요청</h1><p>링크가 만료되었거나 잘못된 접근입니다.</p>";
        }
    }

    @PostMapping("reset-password-request")
    public ResponseEntity<?> requestPasswordReset(String email) throws MessagingException {
        try {
            mailService.sendResetMail(email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("비밀번호 초기화 메일이 발송되었습니다.");
    }
}
