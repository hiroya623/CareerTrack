
package com.careertrack.model;

import java.util.Date;

/**
 * 応募情報のステータス変更履歴を管理するモデルクラス。
 *
 * 応募情報のステータスが変更された際に、
 * 変更前後のステータスや変更日時などを保持する。
 */
public class StatusHistory {

    /**
     * 変更前のステータス。
     *
     * 新規登録時は変更前のステータスが存在しないため、
     * nullを設定する。
     */
    private String previousStatus;

    /**
     * 変更後のステータス。
     *
     * 応募情報が現在どの選考段階に進んだかを保持する。
     */
    private String newStatus;

    /**
     * ステータスを変更した日時。
     */
    private Date changedAt;

    /**
     * ステータス変更時の補足メモ。
     */
    private String memo;

    /**
     * Firestoreからデータを読み込む際に使用する
     * 引数なしコンストラクタ。
     */
    public StatusHistory() {
    }

    /**
     * 変更前のステータスを取得する。
     *
     * @return 変更前のステータス
     */
    public String getPreviousStatus() {
        return previousStatus;
    }

    /**
     * 変更前のステータスを設定する。
     *
     * @param previousStatus 変更前のステータス
     */
    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    /**
     * 変更後のステータスを取得する。
     *
     * @return 変更後のステータス
     */
    public String getNewStatus() {
        return newStatus;
    }

    /**
     * 変更後のステータスを設定する。
     *
     * @param newStatus 変更後のステータス
     */
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    /**
     * ステータス変更日時を取得する。
     *
     * @return ステータス変更日時
     */
    public Date getChangedAt() {
        return changedAt;
    }

    /**
     * ステータス変更日時を設定する。
     *
     * @param changedAt ステータス変更日時
     */
    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    /**
     * 補足メモを取得する。
     *
     * @return 補足メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 補足メモを設定する。
     *
     * @param memo ステータス変更時の補足メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
}
