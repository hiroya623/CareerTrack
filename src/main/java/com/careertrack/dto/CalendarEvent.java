
package com.careertrack.dto;

import java.util.Date;

/**
 * カレンダー画面に表示する予定情報を管理するDTOクラス。
 *
 * 面談・面接や書類提出予定など、
 * 異なるModelの情報を共通の形式で管理する。
 *
 * Firestoreへの保存には使用しない。
 */
public class CalendarEvent {

    /**
     * 予定の種類
     *
     * INTERVIEW：面談・面接
     * DOCUMENT_PLAN：書類提出予定
     * DOCUMENT_DEADLINE：書類提出期限
     */
    private String eventType;

    /**
     * 予定の名称
     */
    private String title;

    /**
     * 予定の日付
     */
    private Date eventDate;

    /**
     * 関連する企業ID
     */
    private String companyId;

    /**
     * 関連する企業名
     */
    private String companyName;

    /**
     * 元データのID
     */
    private String sourceId;

    /**
     * 週表示における予定の開始時刻
     *
     * 例：
     * 10:30
     * 23:00
     * 25:30（翌日の01:30）
     */
    private String displayTime;

    /**
     * 時間軸の開始時刻（7:00）からの経過分数
     *
     * 7:00 ：0
     * 8:00 ：60
     * 10:30 ：210
     * 25:30 ：1110
     */
    private int startMinutes;

    /**
     * カレンダー上に表示する予定の長さ（分）
     *
     * 面談・面接は原則60分とする。
     */
    private int durationMinutes = 60;

    /**
     * デフォルトコンストラクタ
     */
    public CalendarEvent() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public void setStartMinutes(int startMinutes) {
        this.startMinutes = startMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
