
package com.careertrack.model;

import java.util.Date;

/**
 * 企業情報を管理するModelクラス
 *
 * Firestoreのcompaniesコレクションに保存する
 * 企業情報のデータ構造を定義する。
 */
public class Company {

    /**
     * 企業名
     */
    private String companyName;

    /**
     * 企業公式サイトのURL
     */
    private String companyUrl;

    /**
     * 企業の所在地
     */
    private String location;

    /**
     * 企業概要・事業内容
     */
    private String overview;

    /**
     * 自分用の企業メモ
     */
    private String memo;

    /**
     * 企業情報の登録日時
     */
    private Date createdAt;

    /**
     * 企業情報の最終更新日時
     */
    private Date updatedAt;

    /**
     * デフォルトコンストラクタ
     *
     * Firestoreからデータを取得する際に使用する。
     */
    public Company() {
    }

    /**
     * 企業名を取得する
     *
     * @return 企業名
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * 企業名を設定する
     *
     * @param companyName 企業名
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 企業公式サイトのURLを取得する
     *
     * @return 企業公式サイトのURL
     */
    public String getCompanyUrl() {
        return companyUrl;
    }

    /**
     * 企業公式サイトのURLを設定する
     *
     * @param companyUrl 企業公式サイトのURL
     */
    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    /**
     * 所在地を取得する
     *
     * @return 所在地
     */
    public String getLocation() {
        return location;
    }

    /**
     * 所在地を設定する
     *
     * @param location 所在地
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 企業概要を取得する
     *
     * @return 企業概要
     */
    public String getOverview() {
        return overview;
    }

    /**
     * 企業概要を設定する
     *
     * @param overview 企業概要
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * 自分用メモを取得する
     *
     * @return 自分用メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 自分用メモを設定する
     *
     * @param memo 自分用メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 登録日時を取得する
     *
     * @return 登録日時
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 登録日時を設定する
     *
     * @param createdAt 登録日時
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 最終更新日時を取得する
     *
     * @return 最終更新日時
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 最終更新日時を設定する
     *
     * @param updatedAt 最終更新日時
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
