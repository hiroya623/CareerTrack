
package com.careertrack.repository;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.careertrack.model.AppUser;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

/**
 * ユーザー情報をFirestoreで管理するRepository
 *
 * usersコレクションに対する保存・検索処理を担当する。
 */
@Repository
public class UserRepository {

    // Firestore接続用インスタンス
    private final Firestore firestore;

    // ユーザー情報を保存するコレクション名
    private static final String COLLECTION_NAME = "users";

    /**
     * Firestoreインスタンスを受け取る
     *
     * @param firestore Firestore接続用インスタンス
     */
    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * ユーザー情報をFirestoreに保存する
     *
     * @param user 保存するユーザー情報
     */
    public void save(AppUser user) {

        try {
            firestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user)
                .get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "ユーザー情報の保存が中断されました。", e
            );

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                "ユーザー情報の保存に失敗しました。", e
            );
        }
    }

    /**
     * ユーザーIDからユーザー情報を取得する
     *
     * @param userId ユーザーID
     * @return ユーザー情報。存在しない場合はnull
     */
    public AppUser findById(String userId) {

        try {
            DocumentSnapshot snapshot = firestore
                .collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .get();

            if (!snapshot.exists()) {
                return null;
            }

            return snapshot.toObject(AppUser.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "ユーザー情報の取得が中断されました。", e
            );

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                "ユーザー情報の取得に失敗しました。", e
            );
        }
    }

    /**
     * メールアドレスからユーザー情報を取得する
     *
     * @param email メールアドレス
     * @return ユーザー情報。存在しない場合はnull
     */
    public AppUser findByEmail(String email) {

        try {
            var querySnapshot = firestore
                .collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .get();

            if (querySnapshot.isEmpty()) {
                return null;
            }

            return querySnapshot.getDocuments()
                .get(0)
                .toObject(AppUser.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "メールアドレスによる検索が中断されました。", e
            );

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                "メールアドレスによる検索に失敗しました。", e
            );
        }
    }

    /**
     * メールアドレスが登録済みか確認する
     *
     * @param email 確認するメールアドレス
     * @return 登録済みの場合true
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }
}
