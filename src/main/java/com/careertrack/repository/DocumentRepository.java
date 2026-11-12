
package com.careertrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.careertrack.model.Document;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

/**
 * 企業ごとの書類提出情報をFirestoreで管理するRepositoryクラス。
 *
 * 書類ファイル本体ではなく、書類名や提出状況などの管理情報を保存する。
 *
 * Firestore保存先：
 * users/{userId}/companies/{companyId}/documents/{documentId}
 */
@Repository
public class DocumentRepository {

    /**
     * Firestoreの企業コレクション名
     */
    private static final String COMPANIES_COLLECTION = "companies";

    /**
     * Firestoreの書類コレクション名
     */
    private static final String DOCUMENTS_COLLECTION = "documents";

    /**
     * Firestore接続オブジェクト
     */
    private final Firestore firestore;

    /**
     * Firestoreを受け取るコンストラクタ。
     *
     * @param firestore Firestore接続オブジェクト
     */
    public DocumentRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * 指定された企業の書類情報コレクションを取得する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     * @return 書類情報コレクション
     */
    private CollectionReference getCollection(String userId, String companyId) {

        return firestore.collection("users")
                .document(userId)
                .collection(COMPANIES_COLLECTION)
                .document(companyId)
                .collection(DOCUMENTS_COLLECTION);
    }

    /**
     * 書類情報をFirestoreに新規登録または更新する。
     *
     * documentIdが未指定の場合はUUIDで新しいIDを発行する。
     * 指定されている場合は該当する書類情報を更新する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID（新規登録の場合はnullまたは空）
     * @param document   保存する書類情報
     * @return 保存した書類ID
     */
    public String save(
            String userId,
            String companyId,
            String documentId,
            Document document) {

        try {

            // 新規登録の場合はUUIDでIDを発行する
            if (documentId == null || documentId.isBlank()) {
                documentId = UUID.randomUUID().toString();
            }

            // 書類情報をFirestoreに保存する
            getCollection(userId, companyId)
                    .document(documentId)
                    .set(document)
                    .get();

            return documentId;

        } catch (InterruptedException e) {

            // 割り込み状態を復元する
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "書類情報の保存中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "書類情報の保存に失敗しました。", e);
        }
    }

    /**
     * 書類IDを指定して書類情報を1件取得する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     * @return 書類情報。存在しない場合はnull
     */
    public Document findById(
            String userId,
            String companyId,
            String documentId) {

        try {

            DocumentSnapshot snapshot = getCollection(userId, companyId)
                    .document(documentId)
                    .get()
                    .get();

            if (!snapshot.exists()) {
                return null;
            }

            // FirestoreのデータをModelに変換する
            Document document = snapshot.toObject(Document.class);

            if (document != null) {

                // FirestoreのドキュメントIDをModelに設定する
                document.setId(snapshot.getId());
            }

            return document;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "書類情報の取得中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "書類情報の取得に失敗しました。", e);
        }
    }

    /**
     * 指定された企業の書類情報をすべて取得する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     * @return 書類情報一覧
     */
    public List<Document> findAll(String userId, String companyId) {

        try {

            QuerySnapshot querySnapshot = getCollection(userId, companyId)
                    .get()
                    .get();

            List<Document> documents = new ArrayList<>();

            // Firestoreの各ドキュメントをModelに変換する
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {

                // FirestoreのデータをModelに変換する
                Document document = snapshot.toObject(Document.class);

                if (document != null) {

                    // FirestoreのドキュメントIDをModelに設定する
                    document.setId(snapshot.getId());

                    // 書類一覧に追加する
                    documents.add(document);
                }
            }

            return documents;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "書類情報一覧の取得中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "書類情報一覧の取得に失敗しました。", e);
        }
    }

    /**
     * 指定された企業の書類情報を削除する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     */
    public void deleteById(
            String userId,
            String companyId,
            String documentId) {

        try {

            getCollection(userId, companyId)
                    .document(documentId)
                    .delete()
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "書類情報の削除中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "書類情報の削除に失敗しました。", e);
        }
    }

    /**
     * 指定された企業の書類情報が存在するか確認する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     * @return 存在する場合はtrue
     */
    public boolean existsById(
            String userId,
            String companyId,
            String documentId) {

        try {

            return getCollection(userId, companyId)
                    .document(documentId)
                    .get()
                    .get()
                    .exists();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "書類情報の存在確認中に処理が中断されました。", e);

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                    "書類情報の存在確認に失敗しました。", e);
        }
    }
}
