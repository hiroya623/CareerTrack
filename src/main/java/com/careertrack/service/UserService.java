
package com.careertrack.service;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careertrack.dto.UserRegistrationRequest;
import com.careertrack.model.AppUser;
import com.careertrack.repository.UserRepository;

/**
 * ユーザーに関する業務処理を管理するService
 */
@Service
public class UserService {

    // ユーザー情報の保存・検索
    private final UserRepository userRepository;

    // パスワードのハッシュ化
    private final PasswordEncoder passwordEncoder;

    /**
     * 必要な依存クラスを受け取る
     */
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 新しいユーザーを登録する
     *
     * @param request ユーザー登録入力データ
     * @return 登録したユーザー情報
     */
    public AppUser register(UserRegistrationRequest request) {

        // メールアドレスの前後の空白を除去し、小文字に統一
        String email = request.getEmail()
            .trim()
            .toLowerCase(Locale.ROOT);

        // メールアドレスの重複確認
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "このメールアドレスは既に登録されています。"
            );
        }

        // ユーザー情報を作成
        AppUser user = new AppUser();

        // UUIDで一意なユーザーIDを生成
        user.setId(UUID.randomUUID().toString());

        // 氏名を設定
        user.setName(request.getName().trim());

        // メールアドレスを設定
        user.setEmail(email);

        // パスワードをBCryptでハッシュ化して保存
        user.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );

        // 登録日時を設定
        user.setCreatedAt(new Date());

        // Firestoreに保存
        userRepository.save(user);

        return user;
    }

    /**
     * メールアドレスからユーザーIDを取得する
     *
     * ログイン中のユーザーを特定し、
     * 企業情報などのユーザー別データを操作する際に使用する。
     *
     * @param email ログイン中のユーザーのメールアドレス
     * @return ユーザーID
     */
    public String findUserIdByEmail(String email) {

        // メールアドレスが未指定の場合はエラーとする
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                "メールアドレスが指定されていません。"
            );
        }

        // メールアドレスの前後の空白を除去し、小文字に統一
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        // Firestoreからメールアドレスに一致するユーザーを取得
        AppUser user = userRepository.findByEmail(normalizedEmail);

        // 該当するユーザーが存在しない場合はエラーとする
        if (user == null) {
            throw new IllegalArgumentException(
                "ログインユーザーが見つかりません。"
            );
        }

        // ユーザーIDを返す
        return user.getId();
    }
}
