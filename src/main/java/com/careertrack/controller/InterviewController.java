
package com.careertrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careertrack.model.Interview;
import com.careertrack.service.InterviewService;
import com.careertrack.service.UserService;

/**
 * 企業ごとの面談・面接情報を管理するControllerクラス。
 *
 * 面談・面接情報の登録・編集・更新・削除を受け付ける。
 */
@Controller
@RequestMapping("/companies/{companyId}/interviews")
public class InterviewController {

    /**
     * 面談・面接情報を管理するService
     */
    private final InterviewService interviewService;

    /**
     * ユーザー情報を管理するService
     */
    private final UserService userService;

    /**
     * コンストラクタ
     *
     * @param interviewService 面談・面接情報Service
     * @param userService      ユーザー情報Service
     */
    public InterviewController(
            InterviewService interviewService,
            UserService userService) {

        this.interviewService = interviewService;
        this.userService = userService;
    }

    /**
     * 面談・面接登録画面を表示する。
     *
     * @param companyId 企業ID
     * @param model     画面表示用データ
     * @return 面談・面接登録画面
     */
    @GetMapping("/new")
    public String registerForm(
            @PathVariable String companyId,
            Model model) {

        // 新規登録用の空の面談・面接情報を画面に渡す
        model.addAttribute("interview", new Interview());

        // 企業IDを画面に渡す
        model.addAttribute("companyId", companyId);

        return "interview-register";
    }

    /**
     * 面談・面接情報を新規登録する。
     *
     * @param companyId      企業ID
     * @param interview      登録する面談・面接情報
     * @param authentication ログイン情報
     * @param model          画面表示用データ
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 登録画面または企業詳細画面
     */
    @PostMapping("/register")
    public String register(
            @PathVariable String companyId,
            @ModelAttribute Interview interview,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 面談・面接情報を登録
            interviewService.save(userId, companyId, interview);

            // 登録成功メッセージを設定
            redirectAttributes.addFlashAttribute(
                    "interviewSuccessMessage",
                    "面談・面接情報を登録しました。");

        } catch (IllegalArgumentException e) {

            // 入力エラーを画面に渡す
            model.addAttribute("errorMessage", e.getMessage());

            // 企業IDを画面に渡す
            model.addAttribute("companyId", companyId);

            // 入力内容を維持したまま登録画面を再表示する
            return "interview-register";
        }

        return "redirect:/companies/" + companyId;
    }

    /**
     * 面談・面接編集画面を表示する。
     *
     * @param companyId      企業ID
     * @param interviewId    面談・面接ID
     * @param authentication ログイン情報
     * @param model          画面表示用データ
     * @return 面談・面接編集画面
     */
    @GetMapping("/{interviewId}/edit")
    public String editForm(
            @PathVariable String companyId,
            @PathVariable String interviewId,
            Authentication authentication,
            Model model) {

        // ログインユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // 編集対象の面談・面接情報を取得
        Interview interview = interviewService.findById(
                userId,
                companyId,
                interviewId);

        model.addAttribute("interview", interview);
        model.addAttribute("companyId", companyId);
        model.addAttribute("interviewId", interviewId);

        return "interview-edit";
    }

    /**
     * 面談・面接情報を更新する。
     *
     * @param companyId      企業ID
     * @param interviewId    面談・面接ID
     * @param interview      更新する面談・面接情報
     * @param authentication ログイン情報
     * @param model          画面表示用データ
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 編集画面または企業詳細画面
     */
    @PostMapping("/{interviewId}/update")
    public String update(
            @PathVariable String companyId,
            @PathVariable String interviewId,
            @ModelAttribute Interview interview,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // URLのIDを更新対象のModelに設定
            interview.setId(interviewId);

            // 面談・面接情報を更新
            interviewService.update(userId, companyId, interview);

            // 更新成功メッセージを設定
            redirectAttributes.addFlashAttribute(
                    "interviewSuccessMessage",
                    "面談・面接情報を更新しました。");

        } catch (IllegalArgumentException e) {

            // 入力エラーを画面に渡す
            model.addAttribute("errorMessage", e.getMessage());

            // 企業IDと面談・面接IDを画面に渡す
            model.addAttribute("companyId", companyId);
            model.addAttribute("interviewId", interviewId);

            // 入力内容を維持したまま編集画面を再表示する
            return "interview-edit";
        }

        return "redirect:/companies/" + companyId;
    }

    /**
     * 面談・面接情報を削除する。
     *
     * @param companyId          企業ID
     * @param interviewId        面談・面接ID
     * @param authentication     ログイン情報
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 企業詳細画面
     */
    @PostMapping("/{interviewId}/delete")
    public String delete(
            @PathVariable String companyId,
            @PathVariable String interviewId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 面談・面接情報を削除
            interviewService.delete(userId, companyId, interviewId);

            redirectAttributes.addFlashAttribute(
                    "interviewSuccessMessage",
                    "面談・面接情報を削除しました。");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "interviewErrorMessage",
                    e.getMessage());
        }

        return "redirect:/companies/" + companyId;
    }
}
