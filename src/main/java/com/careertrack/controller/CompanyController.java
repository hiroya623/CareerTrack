
package com.careertrack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import com.careertrack.dto.CompanyListItem;
import com.careertrack.model.Company;
import com.careertrack.service.CompanyService;
import com.careertrack.service.UserService;
import com.careertrack.service.ApplicationService;
import com.careertrack.dto.ApplicationListItem;
import com.careertrack.model.StatusHistory;
import com.careertrack.service.DocumentService;
import com.careertrack.model.Document;

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
     * 応募情報に関する業務処理
     */
    private final ApplicationService applicationService;
    /**
     * 書類情報に関する業務処理
     */
    private final DocumentService documentService;

    /**
     * コンストラクタ
     *
     * @param companyService     企業情報のService
     * @param applicationService 応募情報のService
     * @param documentService    書類情報のService
     * @param userService        ユーザー情報のService
     */
    public CompanyController(
            CompanyService companyService,
            ApplicationService applicationService,
            DocumentService documentService,
            UserService userService) {

        this.companyService = companyService;
        this.applicationService = applicationService;
        this.documentService = documentService;
        this.userService = userService;
    }

    /**
     * 企業一覧画面を表示する
     *
     * @param authentication ログイン中のユーザー情報
     * @param model          画面に渡すデータ
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
        List<CompanyListItem> companies = companyService.findAll(userId);

        // 企業一覧を画面に渡す
        model.addAttribute("companies", companies);

        return "company-list";
    }

    /**
     * 企業詳細画面を表示する
     *
     * @param companyId          表示対象の企業ID
     * @param authentication     ログイン中のユーザー情報
     * @param model              画面に渡すデータ
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @return 企業詳細画面、または企業一覧画面
     */
    @GetMapping("/{companyId}")
    public String detail(
            @PathVariable String companyId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        // ログイン中のメールアドレスからユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // ログインユーザー本人の企業情報を取得
        Company company = companyService.findById(
                userId,
                companyId);

        // 企業が存在しない場合は企業一覧画面へ戻す
        if (company == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "指定された企業情報が見つかりません。");

            return "redirect:/companies";
        }

        // この企業に紐付く応募情報を取得
        List<ApplicationListItem> applications = applicationService.findByCompanyId(userId, companyId);

        // この企業に紐付く書類情報を取得
        List<Document> documents = documentService.findAll(userId, companyId);

        // 応募情報ごとのステータス変更履歴を格納する
        Map<String, List<StatusHistory>> statusHistories = new java.util.LinkedHashMap<>();

        // 各応募情報の履歴を取得する
        for (ApplicationListItem applicationItem : applications) {

            List<StatusHistory> histories = applicationService.findStatusHistories(
                    userId,
                    applicationItem.getId());

            statusHistories.put(applicationItem.getId(), histories);
        }

        // 企業情報を画面に渡す
        model.addAttribute("company", company);

        // 企業IDを画面に渡す
        model.addAttribute("companyId", companyId);

        // 応募情報一覧を画面に渡す
        model.addAttribute("applications", applications);

        // 書類情報一覧を画面に渡す
        model.addAttribute("documents", documents);

        // 応募情報ごとのステータス変更履歴を画面に渡す
        model.addAttribute("statusHistories", statusHistories);

        return "company-detail";
    }

    /**
     * 企業情報編集画面を表示する
     *
     * @param companyId          編集対象の企業ID
     * @param authentication     ログイン中のユーザー情報
     * @param model              画面に渡すデータ
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @return 企業情報編集画面
     */
    @GetMapping("/{companyId}/edit")
    public String editForm(
            @PathVariable String companyId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        // ログイン中のメールアドレスからユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // ログインユーザー本人の企業情報を取得
        Company company = companyService.findById(
                userId,
                companyId);

        // 企業情報が存在しない場合
        if (company == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "編集対象の企業情報が見つかりません。");

            return "redirect:/companies";
        }

        // 編集対象の企業情報をフォームに設定
        model.addAttribute("company", company);

        // 企業IDを画面に渡す
        model.addAttribute("companyId", companyId);

        return "company-edit";
    }

    /**
     * 企業情報を更新する
     *
     * @param companyId          更新対象の企業ID
     * @param company            更新する企業情報
     * @param authentication     ログイン中のユーザー情報
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @param model              入力画面に渡すデータ
     * @return 更新後は企業詳細画面へ移動
     */
    @PostMapping("/{companyId}/edit")
    public String update(
            @PathVariable String companyId,
            @ModelAttribute("company") Company company,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            // ログイン中のメールアドレスからユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 企業情報を更新
            companyService.update(
                    userId,
                    companyId,
                    company);

            // 更新成功メッセージを設定
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "企業情報を更新しました。");

            // 企業詳細画面へ移動
            return "redirect:/companies/" + companyId;

        } catch (IllegalArgumentException e) {

            // 入力エラーの内容を画面に表示
            model.addAttribute("errorMessage", e.getMessage());

            // 企業IDを画面に渡す
            model.addAttribute("companyId", companyId);

            // 編集画面へ戻る
            return "company-edit";
        }
    }

    /**
     * 企業情報を削除する
     *
     * @param companyId          削除対象の企業ID
     * @param authentication     ログイン中のユーザー情報
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @return 削除後は企業一覧画面へ移動
     */
    @PostMapping("/{companyId}/delete")
    public String delete(
            @PathVariable String companyId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            // ログイン中のメールアドレスからユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // ログインユーザー本人の企業情報を削除
            companyService.delete(userId, companyId);

            // 削除成功メッセージを設定
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "企業情報を削除しました。");

            // 企業一覧画面へ移動
            return "redirect:/companies";

        } catch (IllegalArgumentException e) {

            // 削除できなかった場合のメッセージを設定
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());

            // 企業一覧画面へ移動
            return "redirect:/companies";
        }
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
     * @param company            登録フォームから受け取った企業情報
     * @param authentication     ログイン中のユーザー情報
     * @param redirectAttributes リダイレクト先に渡すメッセージ
     * @param model              入力画面に渡すデータ
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
