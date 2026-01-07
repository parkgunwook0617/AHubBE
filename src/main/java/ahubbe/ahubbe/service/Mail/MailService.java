package ahubbe.ahubbe.service.Mail;

import ahubbe.ahubbe.entity.Auth;
import ahubbe.ahubbe.entity.User;
import ahubbe.ahubbe.repository.AuthRepository;
import ahubbe.ahubbe.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${PASSWORD_RESET_URL}")
    private String passwordResetUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String createCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            key.append(characters.charAt(random.nextInt(characters.length())));
        }
        return key.toString();
    }

    public String createPasswordCode() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^*+=-";
        String allChars = upper + lower + digits + specials;

        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        passwordChars.add(digits.charAt(random.nextInt(digits.length())));
        passwordChars.add(specials.charAt(random.nextInt(specials.length())));

        int remainingLength = 10 - passwordChars.size();
        for (int i = 0; i < remainingLength; i++) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        Collections.shuffle(passwordChars);

        StringBuilder result = new StringBuilder();
        for (char c : passwordChars) {
            result.append(c);
        }

        return result.toString();
    }

    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    @Transactional
    public boolean sendSimpleMessage(String targetEmail) throws MessagingException {
        String authCode = createCode();

        MimeMessage message = createMail(targetEmail, authCode);
        try {
            mailSender.send(message);

            authRepository.deleteByEmail(targetEmail);
            authRepository.save(new Auth(targetEmail, authCode));

            return true;
        } catch (MailException e) {
            return false;
        }
    }

    public void sendResetMail(String targetEmail) throws MessagingException {
        userRepository
                .findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다."));

        String token = UUID.randomUUID().toString();
        String tempPassword = createPasswordCode();

        authRepository.save(new Auth(targetEmail, token, tempPassword));

        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipients(MimeMessage.RecipientType.TO, targetEmail);
        message.setSubject("[AHub] 비밀번호 초기화");

        String resetUrl = passwordResetUrl + token;

        String body =
                "<h3>비밀번호 초기화 요청</h3>"
                        + "<p>아래 버튼을 누르면 임시 비밀번호로 변경됩니다.</p>"
                        + "<a href='"
                        + resetUrl
                        + "'>비밀번호 초기화 승인</a>"
                        + "<p>임시 비밀번호: <b>"
                        + tempPassword
                        + "</b></p>";

        message.setText(body, "UTF-8", "html");
        mailSender.send(message);
    }

    @Transactional
    public void updatePassword(String email, String newRawPassword) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다."));

        String encodedPassword = passwordEncoder.encode(newRawPassword);

        user.setPassword(encodedPassword);
    }

    @Transactional
    public boolean verifyTokenAndResetPassword(String token) {
        Auth auth = authRepository.findByToken(token);

        if (auth == null) {
            return false;
        }

        updatePassword(auth.getEmail(), auth.getTempPassword());

        authRepository.delete(auth);

        return true;
    }

    @Transactional
    public boolean validateCode(String email, String authCode) {
        Auth auth = authRepository.findByEmail(email);
        if (auth.getAuthCode().equals(authCode)) {
            authRepository.delete(auth);
            return true;
        }
        return false;
    }
}
