
package com.careertrack.dto;

import java.util.Date;

/**
 * 応募情報一覧画面に表示するデータを管理するDTOクラス
 *
 * FirestoreのドキュメントIDと応募情報の表示項目をまとめる。
 * Firestoreに保存するApplicationモデルとは役割を分ける。
 */
public class ApplicationListItem {

    /**
     * FirestoreのドキュメントID
     *
     * 編集・削除対象の応募情報を特定するために使用する。
     */
    private String id;

    /**
     * 応募する職種・求人名
     */
    private String jobTitle;

    /**
     * 応募・選考状況
     */
    private String status;

    /**
     * 応募日
     */
    private Date appliedAt;

    /**
     * 応募・選考に関するメモ
     */
    private String memo;

    /**
     * デフォルトコンストラクタ
     */
    public ApplicationListItem() {
    }

    // idの取得・設定
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // jobTitleの取得・設定
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
}
