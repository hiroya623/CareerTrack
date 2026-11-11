
package com.careertrack.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 応募情報を管理するモデルクラス
 *
 * 企業情報（Company）とは別に、求人ごとの応募・選考情報を管理する。
 * 1つの企業に対して複数の応募情報を登録できる。
 *
 * Firestore保存先：
 * users/{userId}/applications/{applicationId}
 */
public class Application {

    /**
     * 関連付ける企業のID
     *
     * users/{userId}/companies/{companyId} の
     * companyIdを格納する。
     */
    private String companyId;

    /**
     * 応募する職種・求人名
     *
     * 例：
     * Webエンジニア
     * バックエンドエンジニア
     */
    private String jobTitle;

    /**
     * 利用した求人サイトのID
     *
     * 将来実装する求人サイト管理機能と関連付ける。
     */
    private String jobSiteId;

    /**
     * 応募・選考状況
     *
     * 使用する値：
     * INTERESTED：気になる企業
     * SCOUT：スカウト
     * CASUAL_INTERVIEW：カジュアル面談
     * DOCUMENTS：書類選考
     * INTERVIEW：面接
     * ACCEPTED：採用
     * REJECTED：不採用
     * DECLINED：辞退
     */
    private String status;

    /**
     * 応募日
     */
    // 応募日（画面から yyyy-MM-dd 形式で受け取る）
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date appliedAt;

    /**
     * 応募・選考に関するメモ
     */
    private String memo;

    /**
     * 応募情報の登録日時
     */
    private Date createdAt;

    /**
     * 応募情報の最終更新日時
     */
    private Date updatedAt;

    /**
     * Firestoreからデータを読み込む際に使用する
     * デフォルトコンストラクタ
     */
    public Application() {
    }

    // companyIdの取得・設定
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    // jobTitleの取得・設定
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    // jobSiteIdの取得・設定
    public String getJobSiteId() {
        return jobSiteId;
    }

    public void setJobSiteId(String jobSiteId) {
        this.jobSiteId = jobSiteId;
    }

    // statusの取得・設定
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // appliedAtの取得・設定
    public Date getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Date appliedAt) {
        this.appliedAt = appliedAt;
    }

    // memoの取得・設定
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    // createdAtの取得・設定
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // updatedAtの取得・設定
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
