
package com.careertrack.model;

import java.util.Date;

/**
 * CareerTrackを利用するユーザーの情報を管理するクラス
 *
 * Firestoreのusersコレクションに保存するデータを定義する。
 */
public class AppUser {

    // ユーザーを識別するID
    private String id;

    // ユーザーの氏名
    private String name;

    // ログインに使用するメールアドレス
    private String email;

    // BCryptでハッシュ化したパスワード
    private String passwordHash;

    // ユーザー登録日時
    private Date createdAt;

    /**
     * Firestoreのデータ変換で使用するデフォルトコンストラクタ
     */
    public AppUser() {
    }

    /**
     * ユーザー情報を取得する
     *
     * @return ユーザーID
     */
    public String getId() {
        return id;
    }

    /**
     * ユーザーIDを設定する
     *
     * @param id ユーザーID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 氏名を取得する
     *
     * @return 氏名
     */
    public String getName() {
        return name;
    }

    /**
     * 氏名を設定する
     *
     * @param name 氏名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * メールアドレスを取得する
     *
     * @return メールアドレス
     */
    public String getEmail() {
        return email;
    }

    /**
     * メールアドレスを設定する
     *
     * @param email メールアドレス
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * ハッシュ化されたパスワードを取得する
     *
     * @return パスワードハッシュ
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * パスワードハッシュを設定する
     *
     * @param passwordHash ハッシュ化されたパスワード
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * ユーザー登録日時を取得する
     *
     * @return 登録日時
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * ユーザー登録日時を設定する
     *
     * @param createdAt 登録日時
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
