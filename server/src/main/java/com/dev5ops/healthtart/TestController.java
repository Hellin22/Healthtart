package com.dev5ops.healthtart;

import com.dev5ops.healthtart.rival.domain.entity.Rival;
import com.dev5ops.healthtart.rival.repository.RivalRepository;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class TestController {

    private final RivalRepository rivalRepository;
    private final UserRepository userRepository;

    @Autowired
    public TestController(RivalRepository rivalRepository, UserRepository userRepository) {
        this.rivalRepository = rivalRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(){
        log.info("test 진입 완료");
        UserEntity user = userRepository.findById("20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f")
                .orElseThrow(IllegalArgumentException::new);

        log.info("user 끝");
        UserEntity rivalUser = userRepository.findById("20241007-f4252be3-45cc-4318-98dd-ed56593cfc53")
                .orElseThrow(IllegalArgumentException::new);

        log.info("rival 끝");
        Rival rival = Rival.builder()
                        .user(user).rivalUser(rivalUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        rivalRepository.save(rival);
        log.info("test 끝");
        return ResponseEntity.status(200).body("히히히히ㅣ");
    }
}
