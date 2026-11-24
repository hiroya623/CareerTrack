package com.careertrack.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.careertrack.model.Interview;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

/**
 * 面談・面接情報をFirestoreで管理するRepositoryクラス。
 *
 * 企業ごとの面談・面接情報について、
 * 登録・取得・更新・削除を行う。
 *
 * Firestore保存先：
 * users/{userId}/companies/{companyId}/interviews/{interviewId}
 */
@Repository
public class InterviewRepository {

    /**
     * Firestoreへの接続に使用するインスタンス
     */
    private final Firestore firestore;

    /**
     * コンストラクタ。
     *
     * @param firestore Firestoreインスタンス
     */
    public InterviewRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * 企業ごとの面談・面接情報コレクションを取得する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @return 面談・面接情報コレクション
     */
    private CollectionReference getCollection(String userId, String companyId) {

        return firestore.collection("users")
                .document(userId)
                .collection("companies")
                .document(companyId)
                .collection("interviews");
    }

    /**
     * 面談・面接情報を新規登録する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @param interview 登録する面談・面接情報
     * @return 登録されたドキュメントID
     */
    public String save(String userId, String companyId, Interview interview) {

        try {
            var documentReference = getCollection(userId, companyId).document();

            interview.setId(documentReference.getId());

            documentReference.set(interview).get();

            return documentReference.getId();

        } catch (Exception e) {
            throw new RuntimeException("面談・面接情報の登録に失敗しました。", e);
        }
    }

    /**
     * 指定した企業の面談・面接情報をすべて取得する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @return 面談・面接情報一覧
     */
    public List<Interview> findAll(String userId, String companyId) {

        try {
            List<Interview> interviews = new ArrayList<>();

            List<QueryDocumentSnapshot> documents = getCollection(userId, companyId)
                    .get()
                    .get()
                    .getDocuments();

            for (QueryDocumentSnapshot document : documents) {

                Interview interview = document.toObject(Interview.class);

                // FirestoreのドキュメントIDをModelに設定する
                interview.setId(document.getId());

                interviews.add(interview);
            }

            return interviews;

        } catch (Exception e) {
            throw new RuntimeException("面談・面接情報の取得に失敗しました。", e);
        }
    }

    /**
     * 指定した面談・面接情報を取得する。
     *
     * @param userId      ユーザーID
     * @param companyId   企業ID
     * @param interviewId 面談・面接ID
     * @return 面談・面接情報。存在しない場合はnull
     */
    public Interview findById(String userId, String companyId, String interviewId) {

        try {
            DocumentSnapshot document = getCollection(userId, companyId)
                    .document(interviewId)
                    .get()
                    .get();

            if (!document.exists()) {
                return null;
            }

            Interview interview = document.toObject(Interview.class);

            if (interview != null) {
                interview.setId(document.getId());
            }

            return interview;

        } catch (Exception e) {
            throw new RuntimeException("面談・面接情報の取得に失敗しました。", e);
        }
    }

    /**
     * 面談・面接情報を更新する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @param interview 更新する面談・面接情報
     */
    public void update(String userId, String companyId, Interview interview) {

        try {
            getCollection(userId, companyId)
                    .document(interview.getId())
                    .set(interview)
                    .get();

        } catch (Exception e) {
            throw new RuntimeException("面談・面接情報の更新に失敗しました。", e);
        }
    }

    /**
     * 面談・面接情報を削除する。
     *
     * @param userId      ユーザーID
     * @param companyId   企業ID
     * @param interviewId 面談・面接ID
     */
    public void delete(String userId, String companyId, String interviewId) {

        try {
            getCollection(userId, companyId)
                    .document(interviewId)
                    .delete()
                    .get();

        } catch (Exception e) {
            throw new RuntimeException("面談・面接情報の削除に失敗しました。", e);
        }
    }
}