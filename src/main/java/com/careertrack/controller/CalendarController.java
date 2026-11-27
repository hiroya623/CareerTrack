
package com.careertrack.controller;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careertrack.service.CalendarService;
import com.careertrack.service.UserService;

/**
 * カレンダー画面に関する処理を管理するControllerクラス。
 *
 * ログインユーザーの予定情報を取得し、
 * 指定された年月のカレンダー画面に渡す。
 */
@Controller
public class CalendarController {

    /**
     * カレンダー情報を取得するService
     */
    private final CalendarService calendarService;

    /**
     * ユーザー情報を取得するService
     */
    private final UserService userService;

    /**
     * コンストラクタ
     *
     * @param calendarService カレンダー情報Service
     * @param userService     ユーザー情報Service
     */
    public CalendarController(
            CalendarService calendarService,
            UserService userService) {

        this.calendarService = calendarService;
        this.userService = userService;
    }

    /**
     * カレンダー画面を表示する。
     *
     * 月表示と週表示を切り替えて表示する。
     *
     * @param year           表示する年
     * @param month          表示する月
     * @param view           表示形式（month / week）
     * @param date           週表示の基準日
     * @param authentication ログイン中のユーザー情報
     * @param model          画面に渡すデータ
     * @return カレンダー画面
     */
    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false, defaultValue = "month") String view,
            @RequestParam(required = false) String date,
            Authentication authentication,
            Model model) {

        // ログインユーザーIDを取得
        String userId = userService.findUserIdByEmail(
                authentication.getName());

        // 現在の日付を取得
        LocalDate today = LocalDate.now();

        // 表示形式がmonthまたはweek以外の場合は月表示にする
        if (!"week".equals(view)) {
            view = "month";
        }

        // 週表示の基準日
        LocalDate selectedDate = today;

        // URLで日付が指定されている場合
        if (date != null && !date.isBlank()) {

            try {
                selectedDate = LocalDate.parse(date);

            } catch (DateTimeParseException e) {

                // 不正な日付の場合は現在の日付を使用
                selectedDate = today;
            }
        }

        // 週表示の場合
        if ("week".equals(view)) {

            // 時間軸の開始時刻と終了時刻
            final int startHour = 7;
            final int endHour = 27;

            // 7:00～27:00の時間軸の目盛りを生成
            List<Integer> calendarHours = IntStream
                    .rangeClosed(startHour, endHour)
                    .boxed()
                    .toList();

            // 時間軸の情報を画面に渡す
            model.addAttribute("calendarHours", calendarHours);
            model.addAttribute("startHour", startHour);
            model.addAttribute("endHour", endHour);

            // 時間軸全体の長さ（1200分）
            model.addAttribute(
                    "totalMinutes",
                    (endHour - startHour) * 60);

            // 表示する週の開始日（日曜日）
            LocalDate weekStart = selectedDate.minusDays(
                    selectedDate.getDayOfWeek().getValue() % 7);

            // 表示する週の最終日（土曜日）
            LocalDate weekEnd = weekStart.plusDays(6);

            // 週表示用カレンダー情報
            model.addAttribute(
                    "calendarDays",
                    calendarService.findWeek(userId, selectedDate));

            // 週の表示期間
            model.addAttribute("periodLabel",
                    weekStart.getYear() + "年"
                            + weekStart.getMonthValue() + "月"
                            + weekStart.getDayOfMonth() + "日 ～ "
                            + weekEnd.getYear() + "年"
                            + weekEnd.getMonthValue() + "月"
                            + weekEnd.getDayOfMonth() + "日");

            // 前週・翌週の移動先
            model.addAttribute("previousDate", weekStart.minusDays(7));
            model.addAttribute("nextDate", weekStart.plusDays(7));

            // 月表示へ切り替える際に使用する年月
            model.addAttribute("currentYear", selectedDate.getYear());
            model.addAttribute("currentMonth", selectedDate.getMonthValue());

        } else {

            // ==============================
            // 月表示
            // ==============================

            // 現在の年月
            YearMonth currentYearMonth = YearMonth.now();

            // 表示する年月
            YearMonth displayYearMonth = currentYearMonth;

            if (year != null && month != null) {

                try {
                    displayYearMonth = YearMonth.of(year, month);

                } catch (DateTimeException e) {

                    // 不正な年月の場合は現在の年月を表示
                    displayYearMonth = currentYearMonth;
                }
            }

            // 前月と翌月
            YearMonth previousMonth = displayYearMonth.minusMonths(1);
            YearMonth nextMonth = displayYearMonth.plusMonths(1);

            // 週表示へ切り替える際の基準日を設定
            if (displayYearMonth.equals(currentYearMonth)) {

                // 今月の場合は今日を基準日にする
                selectedDate = today;

            } else {

                // 今月以外の場合は表示月の1日を基準日にする
                selectedDate = displayYearMonth.atDay(1);
            }

            // 月表示用カレンダー情報
            model.addAttribute(
                    "calendarDays",
                    calendarService.findMonth(
                            userId,
                            displayYearMonth.getYear(),
                            displayYearMonth.getMonthValue()));

            model.addAttribute("currentYear", displayYearMonth.getYear());
            model.addAttribute("currentMonth", displayYearMonth.getMonthValue());

            model.addAttribute("previousYear", previousMonth.getYear());
            model.addAttribute("previousMonth", previousMonth.getMonthValue());

            model.addAttribute("nextYear", nextMonth.getYear());
            model.addAttribute("nextMonth", nextMonth.getMonthValue());

            model.addAttribute(
                    "periodLabel",
                    displayYearMonth.getYear() + "年 "
                            + displayYearMonth.getMonthValue() + "月");
        }

        // 表示形式
        model.addAttribute("view", view);

        // 週表示の基準日
        model.addAttribute("selectedDate", selectedDate);

        // ログインユーザーのメールアドレス
        model.addAttribute("loginEmail", authentication.getName());

        return "calendar";
    }
}
