
package com.careertrack.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careertrack.dto.CompanyListItem;
import com.careertrack.model.Company;
import com.careertrack.service.CompanyService;
import com.careertrack.service.UserService;

/**
 * 企業情報に関する画面・リクエストを管理するController
 */
@Controller
@RequestMapping("/companies")
public class CompanyController {

    /**
     * 企業情報に関する業務処理
     */
    private final CompanyService companyService;

    /**
     * ユーザー情報に関する業務処理
     */
    private final UserService userService;

    /**
     * コンストラクタ
     *
     * @param companyService 企業情報のService
     * @param userService ユーザー情報のService
     */
    public CompanyController(
            CompanyService companyService,
            UserService userService) {

        this.companyService = companyService;
        this.userService = userService;
    }

    /**
     * 企業一覧画面を表示する
     *
     * @param authentication ログイン中のユーザー情報
     * @param model 画面に渡すデータ
     * @return 企業一覧画面
     */
    @GetMapping
    public String list(
            Authentication authentication,
            Model model) {

        // ログイン中のメールアドレスからユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // ログインユーザー本人の企業情報を取得
        List<CompanyListItem> companies =
                companyService.findAll(userId);

        // 企業一覧を画面に渡す
        model.addAttribute("companies", companies);

        return "company-list";
    }

    /**
     * 企業登録画面を表示する
     *
     * @param model 画面に渡すデータ
     * @return 企業登録画面
     */
    @GetMapping("/register")
    public String registerForm(Model model) {

        // 企業登録フォーム用の空のオブジェクトを用意
        model.addAttribute("company", new Company());

        return "company-register";
    }

    /**
     * 企業情報を新規登録する
     *
     * @param company 登録フォームから受け取った企業情報
     * @param authentication ログイン中のユーザー情報
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @param model 入力画面に渡すデータ
     * @return 登録後は企業一覧画面へ移動
     */
    @PostMapping("/register")
    public String register(
            @ModelAttribute("company") Company company,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // ログイン中のメールアドレスからユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 企業情報を登録
            companyService.register(userId, company);

            // 登録成功メッセージを設定
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "企業情報を登録しました。");

            // 企業一覧画面へ移動
            return "redirect:/companies";

        } catch (IllegalArgumentException e) {

            // 入力エラーの内容を登録画面に表示
            model.addAttribute("errorMessage", e.getMessage());

            // 入力内容を保持したまま登録画面へ戻る
            return "company-register";
        }
    }
}
