
package com.careertrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.careertrack.model.Application;
import com.careertrack.dto.ApplicationListItem;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;


/**
 * 応募情報をFirestoreで管理するRepositoryクラス
 *
 * Firestore保存先：
 * users/{userId}/applications/{applicationId}
 *
 * ログインユーザーごとに応募情報を分離して管理する。
 */
@Repository
public class ApplicationRepository {

    /**
     * Firestoreの応募情報コレクション名
     */
    private static final String COLLECTION_NAME = "applications";

    /**
     * Firestore接続オブジェクト
     */
    private final Firestore firestore;

    /**
     * Firestoreを受け取るコンストラクタ
     *
     * @param firestore Firestore接続オブジェクト
     */
    public ApplicationRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * ログインユーザーの応募情報コレクションを取得する
     *
     * @param userId ログインユーザーID
     * @return 応募情報コレクション
     */
    private CollectionReference getCollection(String userId) {

        return firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_NAME);
    }

    /**
     * 応募情報をFirestoreに保存する
     *
     * applicationIdが未指定の場合は新規登録、
     * 指定されている場合は既存データを更新する。
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     * @param application 保存する応募情報
     * @return 保存した応募情報ID
     */
    public String save(
            String userId,
            String applicationId,
            Application application) {

        try {

            // 新規登録の場合はUUIDでIDを発行する
            if (applicationId == null || applicationId.isBlank()) {
                applicationId = UUID.randomUUID().toString();
            }

            // 応募情報をFirestoreに保存する
            getCollection(userId)
                    .document(applicationId)
                    .set(application)
                    .get();

            return applicationId;

        } catch (InterruptedException e) {

            // 割り込み状態を復元する
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "応募情報の保存中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "応募情報の保存に失敗しました。", e);
        }
    }

    /**
     * 応募情報をIDで1件取得する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     * @return 応募情報。存在しない場合はnull
     */
    public Application findById(String userId, String applicationId) {

        try {

            DocumentSnapshot document = getCollection(userId)
                    .document(applicationId)
                    .get()
                    .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(Application.class);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "応募情報の取得中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "応募情報の取得に失敗しました。", e);
        }
    }

    /**
     * ログインユーザーの応募情報を全件取得する
     *
     * @param userId ログインユーザーID
     * @return 応募情報一覧
     */
    public List<Application> findAll(String userId) {

        try {

            QuerySnapshot querySnapshot = getCollection(userId)
                    .get()
                    .get();

            List<Application> applications = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                Application application =
                        document.toObject(Application.class);

                if (application != null) {
                    applications.add(application);
                }
            }

            return applications;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "応募情報一覧の取得中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "応募情報一覧の取得に失敗しました。", e);
        }
    }

    /**
     * 指定した企業に紐付く応募情報を取得する
     *
     * FirestoreのドキュメントIDも取得し、
     * 一覧画面で編集・削除に使用できるようにする。
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     * @return 指定企業の応募情報一覧
     */
    public List<ApplicationListItem> findByCompanyId(
            String userId,
            String companyId) {

        try {

            QuerySnapshot querySnapshot = getCollection(userId)
                    .whereEqualTo("companyId", companyId)
                    .get()
                    .get();

            List<ApplicationListItem> applications = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                // Firestoreの保存データをApplicationモデルに変換
                Application application =
                        document.toObject(Application.class);

                if (application != null) {

                    // 一覧表示用DTOを作成
                    ApplicationListItem item = new ApplicationListItem();

                    // FirestoreのドキュメントIDを設定
                    item.setId(document.getId());

                    // 応募情報の表示項目を設定
                    item.setJobTitle(application.getJobTitle());
                    item.setStatus(application.getStatus());
                    item.setAppliedAt(application.getAppliedAt());
                    item.setMemo(application.getMemo());

                    applications.add(item);
                }
            }

            return applications;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "企業別応募情報の取得中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "企業別応募情報の取得に失敗しました。", e);
        }
    }

    /**
     * 応募情報を削除する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     */
    public void deleteById(String userId, String applicationId) {

        try {

            getCollection(userId)
                    .document(applicationId)
                    .delete()
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "応募情報の削除中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "応募情報の削除に失敗しました。", e);
        }
    }

    /**
     * 応募情報が存在するか確認する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     * @return 存在する場合はtrue
     */
    public boolean existsById(String userId, String applicationId) {

        try {

            return getCollection(userId)
                    .document(applicationId)
                    .get()
                    .get()
                    .exists();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "応募情報の存在確認中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "応募情報の存在確認に失敗しました。", e);
        }
    }
}
