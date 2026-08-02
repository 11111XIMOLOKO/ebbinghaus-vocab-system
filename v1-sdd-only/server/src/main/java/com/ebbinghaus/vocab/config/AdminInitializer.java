package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.domain.entity.User;
import com.ebbinghaus.vocab.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ROLE_ADMIN = "ADMIN";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 幂等：仅在 admin 用户不存在时创建
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, ADMIN_USERNAME)
        );

        if (count != null && count > 0) {
            log.info("Admin account already exists — skipping initialization");
            return;
        }

        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(ROLE_ADMIN);
        admin.setStatus(1);

        userMapper.insert(admin);
        log.info("Admin account created successfully: {} / {}", ADMIN_USERNAME, ADMIN_PASSWORD);
    }
}
