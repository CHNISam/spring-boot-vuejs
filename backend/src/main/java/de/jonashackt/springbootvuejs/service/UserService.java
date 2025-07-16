package de.jonashackt.springbootvuejs.service;

import org.springframework.stereotype.Service;

/**
 * 临时版 UserService：
 * - currentUserId() 永远返回 1L
 * - findUsername() 返回一个示例名称
 *
 * 后续集成 Spring Security 后，再把这里改成从 SecurityContext 中读取实际用户。
 */
@Service
public class UserService {

    /** 临时固定：当前用户 ID 始终是 1 */
    public Long currentUserId() {
        return 1L;
    }

    /** 临时固定：用户名根据 ID 简单拼接 */
    public String findUsername(Long userId) {
        return "user" + userId;
    }
}
