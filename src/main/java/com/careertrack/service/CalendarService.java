
package com.careertrack.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.careertrack.dto.CalendarEvent;
import com.careertrack.dto.CompanyListItem;
import com.careertrack.model.Document;
import com.careertrack.model.Interview;
import com.careertrack.repository.CompanyRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Comparator;

import com.careertrack.dto.CalendarDay;

/**
 * カレンダー画面に表示する予定情報を取得・集約するサービスクラス。
 *
 * 企業ごとに保存されている面談・面接情報と書類情報を取得し、
 * CalendarEventに変換してまとめる。
 *
 * カレンダー専用のデータはFirestoreに保存しない。
 */
@Service
public class CalendarService {

    private final CompanyRepository companyRepository;
    private final InterviewService interviewService;
    private final DocumentService documentService;

    /**
     * コンストラクタ
     */
    public CalendarService(
            CompanyRepository companyRepository,
            InterviewService interviewService,
            DocumentService documentService) {

        this.companyRepository = companyRepository;
        this.interviewService = interviewService;
        this.documentService = documentService;
    }

    /**
     * ユーザーの予定情報をすべて取得する。
     *
     * @param userId ログインユーザーID
     * @return カレンダー表示用の予定一覧
     */
    public List<CalendarEvent> findAll(String userId) {

        // カレンダーに表示する予定一覧
        List<CalendarEvent> events = new ArrayList<>();

        // ログインユーザーが登録している企業一覧を取得
        List<CompanyListItem> companies = companyRepository.findAll(userId);

        // 企業ごとに面談・面接と書類の情報を取得
        for (CompanyListItem company : companies) {

            String companyId = company.getId();
            String companyName = company.getCompanyName();

            // ==============================
            // 面談・面接予定
            // ==============================

            List<Interview> interviews = interviewService.findAll(userId, companyId);

            for (Interview interview : interviews) {

                // 面談・面接日時が登録されている場合のみ追加
                if (interview.getInterviewDateTime() != null) {

                    CalendarEvent event = new CalendarEvent();

                    event.setEventType("INTERVIEW");
                    event.setTitle(interview.getInterviewName());
                    event.setEventDate(interview.getInterviewDateTime());
                    event.setCompanyId(companyId);
                    event.setCompanyName(companyName);
                    event.setSourceId(interview.getId());

                    events.add(event);
                }
            }

            // ==============================
            // 書類提出予定・提出期限
            // ==============================

            List<Document> documents = documentService.findAll(userId, companyId);

            for (Document document : documents) {

                // 提出済みの書類はカレンダーに表示しない
                if ("SUBMITTED".equals(document.getSubmissionStatus())) {
                    continue;
                }

                // 書類提出予定日
                if (document.getPlannedSubmissionDate() != null) {

                    CalendarEvent event = new CalendarEvent();

                    event.setEventType("DOCUMENT_PLAN");
                    event.setTitle(
                            document.getDocumentName() + "（提出予定）");
                    event.setEventDate(document.getPlannedSubmissionDate());
                    event.setCompanyId(companyId);
                    event.setCompanyName(companyName);
                    event.setSourceId(document.getId());

                    events.add(event);
                }

                // 書類提出期限
                if (document.getDeadlineDate() != null) {

                    CalendarEvent event = new CalendarEvent();

                    event.setEventType("DOCUMENT_DEADLINE");
                    event.setTitle(
                            document.getDocumentName() + "（提出期限）");
                    event.setEventDate(document.getDeadlineDate());
                    event.setCompanyId(companyId);
                    event.setCompanyName(companyName);
                    event.setSourceId(document.getId());

                    events.add(event);
                }
            }
        }

        return events;
    }

    /**
     * 指定された年月のカレンダー情報を作成する。
     *
     * 日曜日始まりの6週間分、合計42日分の日付を生成する。
     *
     * @param userId ログインユーザーID
     * @param year   表示する年
     * @param month  表示する月
     * @return 42日分のカレンダー情報
     */
    public List<CalendarDay> findMonth(
            String userId,
            int year,
            int month) {

        // 表示する年月を作成
        YearMonth yearMonth = YearMonth.of(year, month);

        // 対象月の1日を取得
        LocalDate firstDay = yearMonth.atDay(1);

        // カレンダーの開始日（日曜日）を取得
        LocalDate calendarStart = firstDay.minusDays(
                firstDay.getDayOfWeek().getValue() % 7);

        // 対象月の予定一覧を取得
        List<CalendarEvent> events = findAll(userId);

        // カレンダーの日付一覧
        List<CalendarDay> calendarDays = new ArrayList<>();

        // 6週間分の日付を作成
        for (int i = 0; i < 42; i++) {

            LocalDate date = calendarStart.plusDays(i);

            CalendarDay calendarDay = new CalendarDay();

            calendarDay.setDate(date);
            calendarDay.setDayNumber(date.getDayOfMonth());

            // 表示対象の月に含まれる日付かどうか
            calendarDay.setCurrentMonth(
                    YearMonth.from(date).equals(yearMonth));

            // その日に該当する予定を取得
            List<CalendarEvent> dailyEvents = new ArrayList<>();

            for (CalendarEvent event : events) {

                if (event.getEventDate() == null) {
                    continue;
                }

                // Date型をシステムのタイムゾーンでLocalDateに変換
                LocalDate eventDate = event.getEventDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (date.equals(eventDate)) {
                    dailyEvents.add(event);
                }
            }

            calendarDay.setEvents(dailyEvents);

            calendarDays.add(calendarDay);
        }

        return calendarDays;
    }

    /**
     * 指定された日付を含む週のカレンダー情報を作成する。
     *
     * 日曜日始まりの7日分の日付を生成する。
     * 面談・面接は7:00～27:00の時間軸に配置する。
     *
     * @param userId       ログインユーザーID
     * @param selectedDate 表示する週に含める日付
     * @return 7日分のカレンダー情報
     */
    public List<CalendarDay> findWeek(
            String userId,
            LocalDate selectedDate) {

        // 時間軸の開始時刻（7:00）
        final int startHour = 7;

        // 時間軸の終了時刻（27:00）
        final int endHour = 27;

        // 面談・面接の表示時間（60分）
        final int defaultDuration = 60;

        // 指定された日付の週の日曜日を取得
        LocalDate weekStart = selectedDate.minusDays(
                selectedDate.getDayOfWeek().getValue() % 7);

        // ユーザーの予定一覧を取得
        List<CalendarEvent> events = findAll(userId);

        // 週表示用の日付一覧
        List<CalendarDay> calendarDays = new ArrayList<>();

        // 1週間分の日付を作成
        for (int i = 0; i < 7; i++) {

            LocalDate date = weekStart.plusDays(i);

            CalendarDay calendarDay = new CalendarDay();

            calendarDay.setDate(date);
            calendarDay.setDayNumber(date.getDayOfMonth());
            calendarDay.setCurrentMonth(true);

            // その日に表示する予定一覧
            List<CalendarEvent> dailyEvents = new ArrayList<>();

            for (CalendarEvent event : events) {

                // 日時が設定されていない予定は除外
                if (event.getEventDate() == null) {
                    continue;
                }

                // Date型をLocalDateTime型に変換
                LocalDateTime eventDateTime = event.getEventDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // 予定の日付
                LocalDate eventDate = eventDateTime.toLocalDate();

                // ==============================
                // 面談・面接予定
                // ==============================

                if ("INTERVIEW".equals(event.getEventType())) {

                    // 開始時刻の時・分を取得
                    int hour = eventDateTime.getHour();
                    int minute = eventDateTime.getMinute();

                    // 0:00～2:59は前日の24:00～26:59として扱う
                    if (hour < 3) {

                        eventDate = eventDate.minusDays(1);
                        hour += 24;
                    }

                    // 表示対象の日付でなければ除外
                    if (!date.equals(eventDate)) {
                        continue;
                    }

                    // 時間軸の表示範囲外の予定は除外
                    if (hour < startHour || hour >= endHour) {
                        continue;
                    }

                    // 7:00からの経過分数を計算
                    int startMinutes = (hour - startHour) * 60 + minute;

                    // 時間軸全体の長さ（1200分）
                    int totalMinutes = (endHour - startHour) * 60;

                    // 27:00を超えないように表示時間を調整
                    int durationMinutes = Math.min(
                            defaultDuration,
                            totalMinutes - startMinutes);

                    // カレンダー表示用の情報を設定
                    event.setDisplayTime(
                            String.format("%02d:%02d", hour, minute));

                    event.setStartMinutes(startMinutes);
                    event.setDurationMinutes(durationMinutes);

                    dailyEvents.add(event);

                } else {

                    // ==============================
                    // 書類提出予定・提出期限
                    // ==============================

                    // 書類情報は登録された日付に表示する
                    // 深夜帯の日付調整は行わない
                    if (date.equals(eventDate)) {
                        dailyEvents.add(event);
                    }
                }
            }

            // 予定を開始位置の昇順に並び替える
            dailyEvents.sort(
                    Comparator.comparingInt(
                            CalendarEvent::getStartMinutes));

            calendarDay.setEvents(dailyEvents);

            calendarDays.add(calendarDay);
        }

        return calendarDays;
    }

}
