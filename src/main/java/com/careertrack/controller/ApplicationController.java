
package com.careertrack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careertrack.model.Application;
import com.careertrack.model.Company;
import com.careertrack.service.ApplicationService;
import com.careertrack.service.CompanyService;
import com.careertrack.service.UserService;

/**
 * 応募情報に関する画面と処理を管理するControllerクラス
 *
 * 企業情報に紐付く応募情報の登録・編集・削除を担当する。
 */
@Controller
@RequestMapping("/companies/{companyId}/applications")
public class ApplicationController {

        /**
         * 応募情報Service
         */
        private final ApplicationService applicationService;

        /**
         * 企業情報Service
         */
        private final CompanyService companyService;

        /**
         * ユーザー情報Service
         */
        private final UserService userService;

        /**
         * コンストラクタ
         *
         * @param applicationService 応募情報Service
         * @param companyService     企業情報Service
         * @param userService        ユーザー情報Service
         */
        public ApplicationController(
                        ApplicationService applicationService,
                        CompanyService companyService,
                        UserService userService) {

                this.applicationService = applicationService;
                this.companyService = companyService;
                this.userService = userService;
        }

        /**
         * 応募情報登録画面を表示する
         *
         * @param companyId      企業ID
         * @param authentication ログインユーザー情報
         * @param model          画面表示データ
         * @return 応募情報登録画面
         */
        @GetMapping("/register")
        public String registerForm(
                        @PathVariable String companyId,
                        Authentication authentication,
                        Model model) {

                String userId = userService.findUserIdByEmail(authentication.getName());

                // ログインユーザーの企業情報を取得する
                Company company = companyService.findById(userId, companyId);

                if (company == null) {
                        return "redirect:/companies";
                }

                // 登録フォームに初期値を設定する
                Application application = new Application();
                application.setCompanyId(companyId);
                application.setStatus("INTERESTED");

                model.addAttribute("company", company);
                model.addAttribute("companyId", companyId);
                model.addAttribute("application", application);
                model.addAttribute("statuses", getStatuses());

                return "application-register";
        }

        /**
         * 応募情報を新規登録する
         *
         * @param companyId          企業ID
         * @param application        登録する応募情報
         * @param authentication     ログインユーザー情報
         * @param redirectAttributes リダイレクト先に渡すメッセージ
         * @return 登録後は企業詳細画面へ移動
         */
        @PostMapping("/register")
        public String register(
                        @PathVariable String companyId,
                        @ModelAttribute Application application,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {

                try {

                        String userId = userService.findUserIdByEmail(authentication.getName());

                        // URLの企業IDを使用して関連付ける
                        application.setCompanyId(companyId);

                        applicationService.register(userId, application);

                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "応募情報を登録しました。");

                        return "redirect:/companies/" + companyId;

                } catch (IllegalArgumentException e) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        e.getMessage());

                        return "redirect:/companies/" + companyId
                                        + "/applications/register";
                }
        }

        /**
         * 応募情報編集画面を表示する
         *
         * @param companyId          企業ID
         * @param applicationId      応募情報ID
         * @param authentication     ログインユーザー情報
         * @param model              画面表示データ
         * @param redirectAttributes リダイレクト先に渡すメッセージ
         * @return 応募情報編集画面
         */
        @GetMapping("/{applicationId}/edit")
        public String editForm(
                        @PathVariable String companyId,
                        @PathVariable String applicationId,
                        Authentication authentication,
                        Model model,
                        RedirectAttributes redirectAttributes) {

                String userId = userService.findUserIdByEmail(authentication.getName());

                // 企業情報を取得する
                Company company = companyService.findById(userId, companyId);

                // 応募情報を取得する
                Application application = applicationService.findById(userId, applicationId);

                // 企業または応募情報が存在しない場合は企業一覧へ戻す
                if (company == null
                                || application == null
                                || !companyId.equals(application.getCompanyId())) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "編集対象の応募情報が見つかりません。");

                        return "redirect:/companies";
                }

                model.addAttribute("company", company);
                model.addAttribute("companyId", companyId);
                model.addAttribute("applicationId", applicationId);
                model.addAttribute("application", application);
                model.addAttribute("statuses", getStatuses());

                return "application-edit";
        }

        /**
         * 応募情報を更新する
         *
         * @param companyId          企業ID
         * @param applicationId      応募情報ID
         * @param application        更新する応募情報
         * @param authentication     ログインユーザー情報
         * @param redirectAttributes リダイレクト先に渡すメッセージ
         * @return 更新後は企業詳細画面へ移動
         */
        @PostMapping("/{applicationId}/edit")
        public String update(
                        @PathVariable String companyId,
                        @PathVariable String applicationId,
                        @ModelAttribute Application application,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {

                try {

                        String userId = userService.findUserIdByEmail(authentication.getName());

                        // URLの企業IDを応募情報に設定する
                        application.setCompanyId(companyId);

                        applicationService.update(
                                        userId,
                                        applicationId,
                                        application);

                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "応募情報を更新しました。");

                        return "redirect:/companies/" + companyId;

                } catch (IllegalArgumentException e) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        e.getMessage());

                        return "redirect:/companies/" + companyId;
                }
        }

        /**
         * 応募情報を削除する
         *
         * @param companyId          企業ID
         * @param applicationId      応募情報ID
         * @param authentication     ログインユーザー情報
         * @param redirectAttributes リダイレクト先に渡すメッセージ
         * @return 削除後は企業詳細画面へ移動
         */
        @PostMapping("/{applicationId}/delete")
        public String delete(
                        @PathVariable String companyId,
                        @PathVariable String applicationId,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {

                try {

                        String userId = userService.findUserIdByEmail(authentication.getName());

                        // 応募情報が指定企業に属しているか確認する
                        Application application = applicationService.findById(userId, applicationId);

                        if (application == null
                                        || !companyId.equals(application.getCompanyId())) {

                                throw new IllegalArgumentException(
                                                "削除対象の応募情報が見つかりません。");
                        }

                        applicationService.delete(userId, applicationId);

                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "応募情報を削除しました。");

                } catch (IllegalArgumentException e) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        e.getMessage());
                }

                return "redirect:/companies/" + companyId;
        }

        /**
         * 応募・選考ステータスの選択肢を取得する
         *
         * @return ステータスの内部値と画面表示名の一覧
         */
        private List<Map<String, String>> getStatuses() {

                return List.of(
                                Map.of("value", "INTERESTED", "label", "気になる企業"),
                                Map.of("value", "SCOUT", "label", "スカウト"),
                                Map.of("value", "CASUAL_INTERVIEW", "label", "カジュアル面談"),
                                Map.of("value", "DOCUMENTS", "label", "書類選考"),
                                Map.of("value", "INTERVIEW", "label", "面接"),
                                Map.of("value", "ACCEPTED", "label", "採用"),
                                Map.of("value", "REJECTED", "label", "不採用"),
                                Map.of("value", "DECLINED", "label", "辞退"));
        }
}
