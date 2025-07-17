package de.jonashackt.springbootvuejs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jonashackt.springbootvuejs.domain.User;
import de.jonashackt.springbootvuejs.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/user/me
     * 返回当前已通过 HTTP Basic Auth 登录的用户信息。
     */
    @GetMapping("/api/user/me")
    public User me(@AuthenticationPrincipal UserDetails principal) {
        Long id = userService.currentUserId();          // 目前返回 1L
        String username = principal.getUsername();      // Basic Auth 的用户名

        // 用 public 构造器实例化实体，再设 ID
        User u = new User(username, "");
        u.setId(id);
        return u;
    }
}
