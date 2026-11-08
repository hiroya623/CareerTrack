
package com.careertrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careertrack.dto.UserRegistrationRequest;
import com.careertrack.service.UserService;

import jakarta.validation.Valid;

/**
 * ユーザー登録に関する画面遷移とリクエストを管理するController
 */
@Controller
public class UserController {

    // ユーザー登録処理を担当するService
    private final UserService userService;

    /**
     * UserServiceを受け取る
     *
     * @param userService ユーザー管理Service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * ユーザー登録画面を表示する
     *
     * @param model 画面に渡すデータ
     * @return ユーザー登録画面
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // 登録フォーム用のDTOを画面に渡す
        model.addAttribute("userRegistrationRequest",
            new UserRegistrationRequest());

        return "register";
    }

    /**
     * ユーザー登録処理を実行する
     *
     * @param request 登録フォームの入力値
     * @param bindingResult 入力チェック結果
     * @param redirectAttributes リダイレクト先へ渡すメッセージ
     * @param model 画面に渡すデータ
     * @return 登録後の遷移先
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRegistrationRequest")
            UserRegistrationRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 入力チェックでエラーがある場合は登録画面へ戻す
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // ユーザー登録処理を実行
            userService.register(request);

            // 登録成功メッセージをログイン画面へ渡す
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "アカウントを登録しました。ログインしてください。"
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            // メールアドレス重複などのエラーを画面に表示
            model.addAttribute("registrationError", e.getMessage());

            return "register";
        }
    }
}
