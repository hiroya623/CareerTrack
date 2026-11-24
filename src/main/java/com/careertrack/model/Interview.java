
package com.careertrack.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 企業ごとの面談・面接情報を管理するModelクラス。
 *
 * 面談・面接の予定日時、実施形式、場所、
 * オンライン接続情報などを管理する。
 *
 * Firestore保存先：
 * users/{userId}/companies/{companyId}/interviews/{interviewId}
 */
public class Interview {

    /**
     * FirestoreのドキュメントID
     */
    private String id;

    /**
     * 面談・面接の名称
     *
     * 例：
     * カジュアル面談
     * 1次面接
     * 2次面接
     * 最終面接
     */
    private String interviewName;

    /**
     * 面談・面接の実施予定日時
     *
     * 画面から yyyy-MM-dd'T'HH:mm 形式で受け取る。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date interviewDateTime;

    /**
     * 面談・面接の実施形式
     *
     * ONSITE：対面
     * ONLINE：オンライン
     */
    private String interviewFormat;

    /**
     * 対面の場合の実施場所
     */
    private String location;

    /**
     * オンラインの場合の接続URL
     */
    private String onlineUrl;

    /**
     * オンライン面談・面接のアクセス情報
     *
     * 例：
     * 会議ID、パスコードなど
     */
    private String accessInfo;

    /**
     * 面談・面接に関するメモ
     */
    private String memo;

    /**
     * 面談・面接情報の登録日時
     */
    private Date createdAt;

    /**
     * 面談・面接情報の最終更新日時
     */
    private Date updatedAt;

    /**
     * デフォルトコンストラクタ。
     *
     * Firestoreからデータを取得する際に使用する。
     */
    public Interview() {
    }

    /**
     * IDを取得する。
     *
     * @return 面談・面接ID
     */
    public String getId() {
        return id;
    }

    /**
     * IDを設定する。
     *
     * @param id 面談・面接ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 面談・面接名を取得する。
     *
     * @return 面談・面接名
     */
    public String getInterviewName() {
        return interviewName;
    }

    /**
     * 面談・面接名を設定する。
     *
     * @param interviewName 面談・面接名
     */
    public void setInterviewName(String interviewName) {
        this.interviewName = interviewName;
    }

    /**
     * 面談・面接日時を取得する。
     *
     * @return 面談・面接日時
     */
    public Date getInterviewDateTime() {
        return interviewDateTime;
    }

    /**
     * 面談・面接日時を設定する。
     *
     * @param interviewDateTime 面談・面接日時
     */
    public void setInterviewDateTime(Date interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    /**
     * 実施形式を取得する。
     *
     * @return 対面またはオンライン
     */
    public String getInterviewFormat() {
        return interviewFormat;
    }

    /**
     * 実施形式を設定する。
     *
     * @param interviewFormat 実施形式
     */
    public void setInterviewFormat(String interviewFormat) {
        this.interviewFormat = interviewFormat;
    }

    /**
     * 実施場所を取得する。
     *
     * @return 実施場所
     */
    public String getLocation() {
        return location;
    }

    /**
     * 実施場所を設定する。
     *
     * @param location 実施場所
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * オンラインURLを取得する。
     *
     * @return オンラインURL
     */
    public String getOnlineUrl() {
        return onlineUrl;
    }

    /**
     * オンラインURLを設定する。
     *
     * @param onlineUrl オンラインURL
     */
    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }

    /**
     * アクセス情報を取得する。
     *
     * @return アクセス情報
     */
    public String getAccessInfo() {
        return accessInfo;
    }

    /**
     * アクセス情報を設定する。
     *
     * @param accessInfo アクセス情報
     */
    public void setAccessInfo(String accessInfo) {
        this.accessInfo = accessInfo;
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
}
