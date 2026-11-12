
package com.careertrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.careertrack.model.StatusHistory;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

/**
 * 応募情報のステータス変更履歴をFirestoreで管理するRepository。
 *
 * ユーザーごとの応募情報に紐づく履歴の保存・取得を担当する。
 */
@Repository
public class StatusHistoryRepository {

    private static final String APPLICATIONS_COLLECTION = "applications";
    private static final String HISTORIES_COLLECTION = "statusHistories";

    private final Firestore firestore;

    /**
     * Firestoreを受け取るコンストラクタ。
     *
     * @param firestore Firestore接続オブジェクト
     */
    public StatusHistoryRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * 指定された応募情報のステータス履歴コレクションを取得する。
     *
     * 保存先：
     * users/{userId}/applications/{applicationId}/statusHistories
     *
     * @param userId ユーザーID
     * @param applicationId 応募情報ID
     * @return 履歴コレクション
     */
    private CollectionReference getCollection(String userId, String applicationId) {
        return firestore.collection("users")
                .document(userId)
                .collection(APPLICATIONS_COLLECTION)
                .document(applicationId)
                .collection(HISTORIES_COLLECTION);
    }

    /**
     * ステータス変更履歴を保存する。
     *
     * @param userId ユーザーID
     * @param applicationId 応募情報ID
     * @param history 保存する履歴
     * @return 発行した履歴ID
     */
    public String save(String userId, String applicationId, StatusHistory history) {

        String historyId = UUID.randomUUID().toString();

        try {
            getCollection(userId, applicationId)
                    .document(historyId)
                    .set(history)
                    .get();

            return historyId;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ステータス履歴の保存が中断されました。", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException("ステータス履歴の保存に失敗しました。", e);
        }
    }

    /**
     * 指定された応募情報のステータス変更履歴をすべて取得する。
     *
     * @param userId ユーザーID
     * @param applicationId 応募情報ID
     * @return ステータス変更履歴一覧
     */
    public List<StatusHistory> findAll(String userId, String applicationId) {

        List<StatusHistory> histories = new ArrayList<>();

        try {
            // 変更日時の昇順で履歴を取得する
            List<QueryDocumentSnapshot> documents = getCollection(userId, applicationId)
                    .orderBy("changedAt")
                    .get()
                    .get()
                    .getDocuments();

            // Firestoreの各ドキュメントをモデルクラスに変換する
            for (QueryDocumentSnapshot document : documents) {
                StatusHistory history = document.toObject(StatusHistory.class);

                if (history != null) {
                    histories.add(history);
                }
            }

            return histories;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ステータス履歴の取得が中断されました。", e);

        } catch (ExecutionException e) {
            throw new IllegalStateException("ステータス履歴の取得に失敗しました。", e);
        }
    }

}
