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
        if (token == null || token.isEmpty()) {
            return "<h1>잘못된 접근</h1><p>인증 토큰이 누락되었습니다.</p>";
        }

        try {
            boolean isSuccess = mailService.verifyTokenAndResetPassword(token);

            if (isSuccess) {
                return "<h1>비밀번호 변경 완료</h1>"
                        + "<p>로그인 페이지로 돌아가 <b>메일로 받은 임시 비밀번호</b>로 로그인하세요.</p>"
                        + "<a href='/login'>로그인 페이지로 이동</a>";
            } else {
                return "<h1>유효하지 않은 요청</h1>"
                        + "<p>링크가 이미 사용되었거나, 만료되었을 수 있습니다. 다시 비밀번호 재설정을 요청해주세요.</p>";
            }
        } catch (Exception e) {
            return "<h1>서버 오류</h1><p>처리 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.</p>";
        }
    }

    @PostMapping("reset-password-request")
    public ResponseEntity<?> requestPasswordReset(String email) throws MessagingException {
        try {
            mailService.sendResetMail(email);
            return ResponseEntity.ok("비밀번호 초기화 메일이 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메일 발송 중 오류가 발생했습니다.");
        }
    }
}
