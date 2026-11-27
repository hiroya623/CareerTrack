
package com.careertrack.dto;

import java.util.Date;

/**
 * タスク一覧画面に表示する情報を管理するDTOクラス。
 *
 * 企業ごとに登録されている以下の情報を集約する。
 *
 * ・応募・選考情報
 * ・提出書類情報
 * ・面談・面接情報
 *
 * タスク専用のデータはFirestoreに保存せず、
 * 既存の登録情報から画面表示用のデータを生成する。
 */
public class TaskListItem {

    /**
     * 元の登録情報のドキュメントID
     *
     * 応募ID、書類ID、面談・面接IDのいずれか。
     */
    private String sourceId;

    /**
     * タスクの分類
     *
     * APPLICATION：応募・選考
     * DOCUMENT：提出書類
     * INTERVIEW：面談・面接
     */
    private String taskType;

    /**
     * 関連企業のID
     *
     * 企業詳細画面への画面遷移に使用する。
     */
    private String companyId;

    /**
     * 関連企業の名称
     */
    private String companyName;

    /**
     * タスクの表示名
     *
     * 例：
     * 応募検討
     * 履歴書の提出
     * 一次面接
     */
    private String title;

    /**
     * 元の登録情報の状況
     *
     * 応募・選考の場合は選考ステータス、
     * 提出書類の場合は提出状況、
     * 面談・面接の場合は実施形式などを格納する。
     */
    private String status;

    /**
     * 対応予定日・予定日時
     *
     * 提出書類の場合：提出予定日
     * 面談・面接の場合：実施予定日時
     * 応募・選考の場合：未設定
     */
    private Date plannedDate;

    /**
     * 対応期限日
     *
     * 提出書類の場合は提出期限日を格納する。
     */
    private Date deadlineDate;

    /**
     * 対応期限を超過しているかどうか
     */
    private boolean deadlineOverdue;

    /**
     * 提出予定日を超過しているかどうか
     */
    private boolean plannedDateOverdue;

    /**
     * デフォルトコンストラクタ
     */
    public TaskListItem() {
    }

    // sourceIdの取得・設定
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    // taskTypeの取得・設定
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    // companyIdの取得・設定
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    // companyNameの取得・設定
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // titleの取得・設定
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // statusの取得・設定
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // plannedDateの取得・設定
    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }

    // deadlineDateの取得・設定
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    // deadlineOverdueの取得・設定
    public boolean isDeadlineOverdue() {
        return deadlineOverdue;
    }

    public void setDeadlineOverdue(boolean deadlineOverdue) {
        this.deadlineOverdue = deadlineOverdue;
    }

    // plannedDateOverdueの取得・設定
    public boolean isPlannedDateOverdue() {
        return plannedDateOverdue;
    }

    public void setPlannedDateOverdue(boolean plannedDateOverdue) {
        this.plannedDateOverdue = plannedDateOverdue;
    }
}
