
package com.careertrack.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careertrack.dto.TaskListItem;
import com.careertrack.service.TaskService;
import com.careertrack.service.UserService;

/**
 * ホーム画面を管理するControllerクラス。
 *
 * 各企業に登録されている以下の情報を集約して表示する。
 *
 * ・応募・選考情報
 * ・提出書類情報
 * ・面談・面接情報
 *
 * 元の情報の登録・編集・削除は企業詳細画面から行う。
 */
@Controller
public class HomeController {

    /**
     * タスク情報を取得・集約するService
     */
    private final TaskService taskService;

    /**
     * ログインユーザー情報を管理するService
     */
    private final UserService userService;

    /**
     * コンストラクタ
     */
    public HomeController(
            TaskService taskService,
            UserService userService) {

        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * ホーム画面を表示する。
     *
     * typeパラメータによって表示対象を切り替える。
     *
     * ALL：すべて
     * APPLICATION：応募・選考
     * DOCUMENT：提出書類
     * INTERVIEW：面談・面接
     */
    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "ALL") String type,
            Authentication authentication,
            Model model) {

        // ログイン中のユーザーのメールアドレスを取得する
        String loginEmail = authentication.getName();

        // ログインユーザーのIDを取得する
        String userId = userService.findUserIdByEmail(loginEmail);

        // ログインユーザーのタスク情報をすべて取得する
        List<TaskListItem> allTasks = taskService.findAll(userId);

        // ==============================
        // 分類ごとの件数を取得
        // ==============================

        // 全タスク件数
        long totalCount = allTasks.size();

        // 応募・選考件数
        long applicationCount = allTasks.stream()
                .filter(task -> "APPLICATION".equals(task.getTaskType()))
                .count();

        // 提出書類件数
        long documentCount = allTasks.stream()
                .filter(task -> "DOCUMENT".equals(task.getTaskType()))
                .count();

        // 面談・面接件数
        long interviewCount = allTasks.stream()
                .filter(task -> "INTERVIEW".equals(task.getTaskType()))
                .count();

        // 提出期限超過件数
        long overdueCount = allTasks.stream()
                .filter(TaskListItem::isDeadlineOverdue)
                .count();

        // ==============================
        // 絞り込み条件の確認
        // ==============================

        // 使用可能な分類
        List<String> allowedTypes = List.of(
                "ALL",
                "APPLICATION",
                "DOCUMENT",
                "INTERVIEW");

        // 不正な分類が指定された場合はすべて表示する
        if (!allowedTypes.contains(type)) {

            type = "ALL";
        }

        // ==============================
        // タスク情報の絞り込み
        // ==============================

        List<TaskListItem> tasks = allTasks;

        // 分類が指定されている場合のみ絞り込む
        if (!"ALL".equals(type)) {

            String selectedType = type;

            tasks = allTasks.stream()
                    .filter(task -> selectedType.equals(task.getTaskType()))
                    .toList();
        }

        // ==============================
        // 画面表示用データの設定
        // ==============================

        model.addAttribute("tasks", tasks);

        model.addAttribute("selectedType", type);

        model.addAttribute("totalCount", totalCount);

        model.addAttribute("applicationCount", applicationCount);

        model.addAttribute("documentCount", documentCount);

        model.addAttribute("interviewCount", interviewCount);

        model.addAttribute("overdueCount", overdueCount);

        model.addAttribute("loginEmail", loginEmail);

        return "dashboard";
    }
}
