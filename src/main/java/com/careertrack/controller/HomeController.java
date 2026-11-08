package com.careertrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面に関する処理を管理するController
 */
@Controller
public class HomeController {

    /**
     * ダッシュボード画面を表示する
     *
     * @param authentication ログイン中のユーザー情報
     * @param model 画面に渡すデータ
     * @return ダッシュボード画面
     */
    @GetMapping("/")
    public String dashboard(Authentication authentication, Model model) {

        // ログイン中のユーザーのメールアドレスを取得する
        model.addAttribute("loginEmail", authentication.getName());

        return "dashboard";
    }
}