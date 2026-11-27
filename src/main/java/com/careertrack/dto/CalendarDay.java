
package com.careertrack.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * カレンダーの1日分の情報を管理するDTOクラス。
 *
 * 日付、表示する日付番号、表示対象月かどうか、
 * その日に登録されている予定一覧を管理する。
 *
 * Firestoreへの保存には使用しない。
 */
public class CalendarDay {

    /**
     * カレンダーに表示する日付
     */
    private LocalDate date;

    /**
     * 日付の数字
     */
    private int dayNumber;

    /**
     * 表示対象の月に含まれる日付かどうか
     *
     * true：表示対象の月
     * false：前月または翌月の日付
     */
    private boolean currentMonth;

    /**
     * その日に登録されている予定一覧
     */
    private List<CalendarEvent> events = new ArrayList<>();

    /**
     * デフォルトコンストラクタ
     */
    public CalendarDay() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.currentMonth = currentMonth;
    }

    public List<CalendarEvent> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
    }
}
