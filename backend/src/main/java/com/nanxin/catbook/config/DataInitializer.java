package com.nanxin.catbook.config;

import com.nanxin.catbook.entity.User;
import com.nanxin.catbook.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // 初始化Administrator账号（如果不存在）
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setNickname("Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(1);
            userRepository.save(admin);
            log.info("Admin account created: admin / admin123");
        } else {
            log.info("Admin account already exists, skipping initialization");
        }
    }
}