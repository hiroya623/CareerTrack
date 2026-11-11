package com.careertrack.dto;

import java.util.Date;

/**
 * 企業一覧画面に表示する情報を管理するDTOクラス
 *
 * FirestoreのドキュメントIDと企業情報をまとめ、
 * 企業一覧画面で使用する。
 *
 * Firestoreへの保存には使用しない。
 */
public class CompanyListItem {

    /**
     * FirestoreのドキュメントID（企業ID）
     */
    private String id;

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
     * 企業情報の最終更新日時
     */
    private Date updatedAt;

    /**
     * デフォルトコンストラクタ
     */
    public CompanyListItem() {
    }

    /**
     * 企業IDを取得する
     *
     * @return 企業ID
     */
    public String getId() {
        return id;
    }

    /**
     * 企業IDを設定する
     *
     * @param id 企業ID
     */
    public void setId(String id) {
        this.id = id;
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
