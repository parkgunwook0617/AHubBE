package ahubbe.ahubbe.service.Mail;

import ahubbe.ahubbe.repository.AuthRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailCleanupScheduler {
    private final AuthRepository authRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanup() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        authRepository.deleteByCreatedDateBefore(fiveMinutesAgo);

        log.info("만료된 인증 코드 삭제 완료");
    }
}
