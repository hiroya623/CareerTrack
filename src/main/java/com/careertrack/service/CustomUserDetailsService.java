package com.careertrack.service;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.careertrack.model.AppUser;
import com.careertrack.repository.UserRepository;

/**
 * Firestoreからユーザー情報を取得する認証用サービス
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * コンストラクタ
     *
     * @param userRepository ユーザー情報を操作するRepository
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * メールアドレスをもとにユーザー情報を取得する
     *
     * @param email ログイン時に入力されたメールアドレス
     * @return Spring Securityが認証に使用するユーザー情報
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // メールアドレスの前後の空白を除去し、小文字に統一する
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        // Firestoreからユーザー情報を取得する
        AppUser appUser = userRepository.findByEmail(normalizedEmail);

        // 該当するユーザーが存在しない場合は認証失敗とする
        if (appUser == null) {
            throw new UsernameNotFoundException(
                "ユーザーが見つかりません。"
            );
        }

        // Firestoreに保存されているBCryptハッシュを返す
        return User.withUsername(appUser.getEmail())
            .password(appUser.getPasswordHash())
            .roles("USER")
            .build();
    }
}