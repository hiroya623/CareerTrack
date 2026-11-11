package com.careertrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログイン画面に関する処理を管理するController
 */
@Controller
public class LoginController {

    /**
     * ログイン画面を表示する
     *
     * @return ログイン画面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}