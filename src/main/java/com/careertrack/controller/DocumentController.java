
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

import com.careertrack.model.Document;
import com.careertrack.service.DocumentService;
import com.careertrack.service.UserService;

/**
 * 企業ごとの書類提出情報を管理するControllerクラス。
 *
 * 書類情報の登録・編集・更新・削除を受け付ける。
 * ファイル本体のアップロード機能は扱わない。
 */
@Controller
@RequestMapping("/companies/{companyId}/documents")
public class DocumentController {

    /**
     * 書類情報を管理するService
     */
    private final DocumentService documentService;

    /**
     * ユーザー情報を管理するService
     */
    private final UserService userService;

    /**
     * コンストラクタ
     *
     * @param documentService 書類情報Service
     * @param userService     ユーザー情報Service
     */
    public DocumentController(
            DocumentService documentService,
            UserService userService) {

        this.documentService = documentService;
        this.userService = userService;
    }

    /**
     * 書類登録画面を表示する。
     *
     * @param companyId 企業ID
     * @param model     画面表示用データ
     * @return 書類登録画面
     */
    @GetMapping("/new")
    public String registerForm(
            @PathVariable String companyId,
            Model model) {

        // 新規登録用の空の書類情報を画面に渡す
        model.addAttribute("document", new Document());

        // 企業IDを画面に渡す
        model.addAttribute("companyId", companyId);

        return "document-register";
    }

    /**
     * 書類情報を新規登録する。
     *
     * @param companyId          企業ID
     * @param document           登録する書類情報
     * @param authentication     ログイン情報
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 企業詳細画面
     */
    @PostMapping("/register")
    public String register(
            @PathVariable String companyId,
            @ModelAttribute Document document,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 書類情報を登録
            documentService.register(userId, companyId, document);

            redirectAttributes.addFlashAttribute(
                    "documentSuccessMessage",
                    "書類情報を登録しました。");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "documentErrorMessage",
                    e.getMessage());
        }

        return "redirect:/companies/" + companyId;
    }

    /**
     * 書類編集画面を表示する。
     *
     * @param companyId      企業ID
     * @param documentId     書類ID
     * @param authentication ログイン情報
     * @param model          画面表示用データ
     * @return 書類編集画面
     */
    @GetMapping("/{documentId}/edit")
    public String editForm(
            @PathVariable String companyId,
            @PathVariable String documentId,
            Authentication authentication,
            Model model) {

        // ログインユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // 編集対象の書類情報を取得
        Document document = documentService.findById(
                userId,
                companyId,
                documentId);

        model.addAttribute("document", document);
        model.addAttribute("companyId", companyId);
        model.addAttribute("documentId", documentId);

        return "document-edit";
    }

    /**
     * 書類情報を更新する。
     *
     * @param companyId          企業ID
     * @param documentId         書類ID
     * @param document           更新する書類情報
     * @param authentication     ログイン情報
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 企業詳細画面
     */
    @PostMapping("/{documentId}/update")
    public String update(
            @PathVariable String companyId,
            @PathVariable String documentId,
            @ModelAttribute Document document,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 書類情報を更新
            documentService.update(
                    userId,
                    companyId,
                    documentId,
                    document);

            redirectAttributes.addFlashAttribute(
                    "documentSuccessMessage",
                    "書類情報を更新しました。");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "documentErrorMessage",
                    e.getMessage());
        }

        return "redirect:/companies/" + companyId;
    }

    /**
     * 書類情報を削除する。
     *
     * @param companyId          企業ID
     * @param documentId         書類ID
     * @param authentication     ログイン情報
     * @param redirectAttributes リダイレクト時のメッセージ
     * @return 企業詳細画面
     */
    @PostMapping("/{documentId}/delete")
    public String delete(
            @PathVariable String companyId,
            @PathVariable String documentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // ログインユーザーIDを取得
            String userId = userService.findUserIdByEmail(
                    authentication.getName());

            // 書類情報を削除
            documentService.delete(userId, companyId, documentId);

            redirectAttributes.addFlashAttribute(
                    "documentSuccessMessage",
                    "書類情報を削除しました。");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "documentErrorMessage",
                    e.getMessage());
        }

        return "redirect:/companies/" + companyId;
    }
}
