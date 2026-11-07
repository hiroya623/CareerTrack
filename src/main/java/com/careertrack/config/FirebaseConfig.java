
package com.careertrack.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

/**
 * Firebaseとの接続設定を管理するクラス
 *
 * Firebase Admin SDKを初期化し、
 * FirestoreをSpring Beanとして利用できるようにする。
 */
@Configuration
public class FirebaseConfig {

    /**
     * Firestore接続用のインスタンスを生成する。
     *
     * @return Firestoreインスタンス
     * @throws IOException 認証情報ファイルの読み込みに失敗した場合
     */
    @Bean
    public Firestore firestore() throws IOException {

        // FirebaseサービスアカウントJSONの保存先を環境変数から取得
        String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");

        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException(
                "環境変数 FIREBASE_CREDENTIALS_PATH が設定されていません。"
            );
        }

        // Firebaseアプリが初期化済みか確認
        if (FirebaseApp.getApps().isEmpty()) {

            // サービスアカウントJSONから認証情報を読み込む
            try (FileInputStream serviceAccount =
                    new FileInputStream(credentialsPath)) {

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                        GoogleCredentials.fromStream(serviceAccount)
                    )
                    .build();

                FirebaseApp.initializeApp(options);
            }
        }

        // Firestoreインスタンスを返す
        return FirestoreClient.getFirestore();
    }
}
