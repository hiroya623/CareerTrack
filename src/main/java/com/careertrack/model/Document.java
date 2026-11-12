
package com.careertrack.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 企業ごとの書類提出情報を管理するModelクラス。
 *
 * Firestoreの以下の場所に保存する。
 * users/{userId}/companies/{companyId}/documents/{documentId}
 *
 * このクラスでは書類ファイル本体は保存せず、
 * 書類の提出状況や提出予定日などの管理情報のみを扱う。
 */
public class Document {

    /**
     * FirestoreのドキュメントID
     *
     * 書類情報を個別に識別し、編集・削除するために使用する。
     */
    private String id;

    /**
     * 書類名
     *
     * 例：履歴書、職務経歴書、スキルシート
     */
    private String documentName;

    /**
     * 提出状況
     *
     * NOT_SUBMITTED：未提出
     * SUBMITTED：提出済み
     */
    private String submissionStatus;

    /**
     * 企業から指定された提出期限日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadlineDate;

    /**
     * 自分で設定した提出予定日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date plannedSubmissionDate;

    /**
     * 実際に書類を提出した日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submittedDate;

    /**
     * 書類に関するメモ
     */
    private String memo;

    /**
     * 書類情報の登録日時
     */
    private Date createdAt;

    /**
     * 書類情報の最終更新日時
     */
    private Date updatedAt;

    /**
     * デフォルトコンストラクタ。
     *
     * Firestoreからデータを取得する際に使用する。
     */
    public Document() {
    }

    /**
     * 書類名を取得する。
     *
     * @return 書類名
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * 書類名を設定する。
     *
     * @param documentName 書類名
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * 提出状況を取得する。
     *
     * @return 提出状況
     */
    public String getSubmissionStatus() {
        return submissionStatus;
    }

    /**
     * 提出状況を設定する。
     *
     * @param submissionStatus 提出状況
     */
    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    /**
     * 提出期限日を取得する。
     *
     * @return 提出期限日
     */
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * 提出期限日を設定する。
     *
     * @param deadlineDate 提出期限日
     */
    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    /**
     * 提出予定日を取得する。
     *
     * @return 提出予定日
     */
    public Date getPlannedSubmissionDate() {
        return plannedSubmissionDate;
    }

    /**
     * 提出予定日を設定する。
     *
     * @param plannedSubmissionDate 提出予定日
     */
    public void setPlannedSubmissionDate(Date plannedSubmissionDate) {
        this.plannedSubmissionDate = plannedSubmissionDate;
    }

    /**
     * 実際の提出日を取得する。
     *
     * @return 実際の提出日
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * 実際の提出日を設定する。
     *
     * @param submittedDate 実際の提出日
     */
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * メモを取得する。
     *
     * @return メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * メモを設定する。
     *
     * @param memo メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 登録日時を取得する。
     *
     * @return 登録日時
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 登録日時を設定する。
     *
     * @param createdAt 登録日時
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 最終更新日時を取得する。
     *
     * @return 最終更新日時
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 最終更新日時を設定する。
     *
     * @param updatedAt 最終更新日時
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 書類IDを取得する。
     *
     * @return 書類ID
     */
    public String getId() {
        return id;
    }

    /**
     * 書類IDを設定する。
     *
     * @param id 書類ID
     */
    public void setId(String id) {
        this.id = id;
    }
}
