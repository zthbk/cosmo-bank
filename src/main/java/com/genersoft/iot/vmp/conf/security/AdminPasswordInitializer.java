package com.genersoft.iot.vmp.conf.security;

import com.genersoft.iot.vmp.service.IUserService;
import com.genersoft.iot.vmp.storager.dao.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 首次部署时通过环境变量设置管理员密码，避免初始化脚本保留可用默认口令。
 * 仅当管理员仍处于默认密码状态时生效，已修改过的密码不会被覆盖。
 */
@Slf4j
@Component
public class AdminPasswordInitializer implements ApplicationRunner {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordComplexityValidator passwordComplexityValidator;

    @Value("${WVP_ADMIN_PASSWORD:}")
    private String initialPassword;

    @Override
    public void run(ApplicationArguments args) {
        User admin = userService.getUserByUsername("admin");
        if (admin == null) {
            return;
        }
        if (!StringUtils.hasText(initialPassword)) {
            if (admin.isDefaultPassword()) {
                log.warn("管理员仍处于默认密码状态，请设置 WVP_ADMIN_PASSWORD 后重启，或登录后立即修改密码");
            }
            return;
        }
        if (!admin.isDefaultPassword()) {
            log.info("管理员密码已修改，忽略 WVP_ADMIN_PASSWORD");
            return;
        }
        if (!passwordComplexityValidator.validatePassword(initialPassword, "admin")) {
            log.error("WVP_ADMIN_PASSWORD 不满足密码复杂度要求，未设置管理员密码");
            return;
        }
        boolean updated = userService.changePassword(admin.getId(),
                DigestUtils.md5DigestAsHex(initialPassword.getBytes(StandardCharsets.UTF_8)));
        if (updated) {
            log.info("已通过 WVP_ADMIN_PASSWORD 设置管理员初始密码");
        } else {
            log.error("设置管理员初始密码失败");
        }
    }
}
