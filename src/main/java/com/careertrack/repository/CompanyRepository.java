
package com.careertrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.careertrack.model.Company;
import com.careertrack.dto.CompanyListItem;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

/**
 * 企業情報のデータ操作を担当するRepositoryクラス
 *
 * Firestoreの以下の場所に企業情報を保存する。
 * users/{userId}/companies/{companyId}
 */
@Repository
public class CompanyRepository {

    /**
     * Firestoreのインスタンス
     */
    private final Firestore firestore;

    /**
     * コンストラクタ
     *
     * @param firestore Firestoreのインスタンス
     */
    public CompanyRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * ユーザーごとの企業情報コレクションを取得する
     *
     * @param userId ログインユーザーID
     * @return 企業情報のコレクション
     */
    private CollectionReference getCollection(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("companies");
    }

    /**
     * 企業情報を新規登録または更新する
     *
     * companyIdがnullまたは空の場合は新規登録としてUUIDを発行する。
     * companyIdが指定されている場合は、そのIDのドキュメントを更新する。
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID（新規登録の場合はnullまたは空）
     * @param company 登録・更新する企業情報
     * @return 保存後の企業ID
     */
    public String save(String userId, String companyId, Company company) {

        // 企業IDが未指定の場合は新規登録としてUUIDを発行する
        if (companyId == null || companyId.isBlank()) {
            companyId = UUID.randomUUID().toString();
        }

        // 保存先のドキュメントを取得する
        DocumentReference document = getCollection(userId)
                .document(companyId);

        try {
            // 企業情報をFirestoreに保存する
            document.set(company).get();

            return companyId;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業情報の保存中に処理が中断されました。", e);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "企業情報の保存に失敗しました。", e);
        }
    }

    /**
     * 企業IDを指定して企業情報を1件取得する
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     * @return 企業情報。存在しない場合はnull
     */
    public Company findById(String userId, String companyId) {

        try {
            DocumentSnapshot document = getCollection(userId)
                    .document(companyId)
                    .get()
                    .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(Company.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業情報の取得中に処理が中断されました。", e);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "企業情報の取得に失敗しました。", e);
        }
    }

    
    /**
     * ログインユーザーの企業情報をすべて取得する
     *
     * FirestoreのドキュメントIDと企業情報をまとめて取得する。
     * ドキュメントIDは画面から詳細画面などへ移動する際に使用する。
     *
     * @param userId ログインユーザーID
     * @return 企業IDを含む企業情報の一覧
     */
    public List<CompanyListItem> findAll(String userId) {

        try {
            // ログインユーザーの企業情報を取得
            QuerySnapshot querySnapshot = getCollection(userId)
                    .get()
                    .get();

            // 取得結果を格納するリスト
            List<CompanyListItem> companies = new ArrayList<>();

            // Firestoreの各ドキュメントを一覧表示用DTOに変換する
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                // 企業情報を取得
                Company company = document.toObject(Company.class);

                // 企業情報が取得できた場合のみ一覧に追加
                if (company != null) {

                    // 一覧表示用DTOを作成
                    CompanyListItem item = new CompanyListItem();

                    // FirestoreのドキュメントIDを企業IDとして設定
                    item.setId(document.getId());

                    // 企業情報を設定
                    item.setCompanyName(company.getCompanyName());
                    item.setCompanyUrl(company.getCompanyUrl());
                    item.setLocation(company.getLocation());
                    item.setUpdatedAt(company.getUpdatedAt());

                    // 一覧に追加
                    companies.add(item);
                }
            }

            return companies;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業一覧の取得中に処理が中断されました。", e);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "企業一覧の取得に失敗しました。", e);
        }
    }

    /**
     * 企業IDを指定して企業情報を削除する
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     */
    public void deleteById(String userId, String companyId) {

        try {
            getCollection(userId)
                    .document(companyId)
                    .delete()
                    .get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業情報の削除中に処理が中断されました。", e);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "企業情報の削除に失敗しました。", e);
        }
    }

    /**
     * 企業IDを指定して企業情報の存在を確認する
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     * @return 存在する場合はtrue
     */
    public boolean existsById(String userId, String companyId) {

        try {
            return getCollection(userId)
                    .document(companyId)
                    .get()
                    .get()
                    .exists();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業情報の存在確認中に処理が中断されました。", e);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "企業情報の存在確認に失敗しました。", e);
        }
    }
}
