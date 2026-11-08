
package com.careertrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録画面から受け取る入力データ
 */
public class UserRegistrationRequest {

    // 氏名
    @NotBlank(message = "氏名を入力してください。")
    @Size(max = 50, message = "氏名は50文字以内で入力してください。")
    private String name;

    // メールアドレス
    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "正しいメールアドレスを入力してください。")
    @Size(max = 254, message = "メールアドレスは254文字以内で入力してください。")
    private String email;

    // パスワード
    @NotBlank(message = "パスワードを入力してください。")
    @Size(min = 8, max = 72, message = "パスワードは8文字以上72文字以内で入力してください。")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
