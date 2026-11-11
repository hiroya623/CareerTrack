
package com.careertrack.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.careertrack.model.Application;
import com.careertrack.repository.ApplicationRepository;
import com.careertrack.repository.CompanyRepository;
import com.careertrack.dto.ApplicationListItem;

/**
 * 応募情報に関する業務処理を管理するServiceクラス
 *
 * 応募情報の入力チェック、登録、更新、取得、削除を担当する。
 * また、応募情報と企業情報の関連付けも管理する。
 */
@Service
public class ApplicationService {

    /**
     * 応募情報Repository
     */
    private final ApplicationRepository applicationRepository;

    /**
     * 企業情報Repository
     */
    private final CompanyRepository companyRepository;

    /**
     * Repositoryを受け取るコンストラクタ
     *
     * @param applicationRepository 応募情報Repository
     * @param companyRepository 企業情報Repository
     */
    public ApplicationService(
            ApplicationRepository applicationRepository,
            CompanyRepository companyRepository) {

        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 応募情報を新規登録する
     *
     * @param userId ログインユーザーID
     * @param application 登録する応募情報
     * @return 登録した応募情報ID
     */
    public String register(String userId, Application application) {

        // 入力内容と関連企業の存在を確認する
        validateAndNormalize(userId, application);

        // 登録日時と更新日時を設定する
        Date now = new Date();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        // 応募情報を保存する
        return applicationRepository.save(userId, null, application);
    }

    /**
     * 応募情報を更新する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     * @param application 更新する応募情報
     */
    public void update(
            String userId,
            String applicationId,
            Application application) {

        // 応募情報IDを確認する
        if (!StringUtils.hasText(applicationId)) {
            throw new IllegalArgumentException(
                    "応募情報IDが指定されていません。");
        }

        // 更新対象の応募情報を取得する
        Application existingApplication =
                applicationRepository.findById(userId, applicationId);

        if (existingApplication == null) {
            throw new IllegalArgumentException(
                    "更新対象の応募情報が見つかりません。");
        }

        // 入力内容と関連企業の存在を確認する
        validateAndNormalize(userId, application);

        // 登録日時は維持し、更新日時だけ変更する
        application.setCreatedAt(existingApplication.getCreatedAt());
        application.setUpdatedAt(new Date());

        // 応募情報を更新する
        applicationRepository.save(userId, applicationId, application);
    }

    /**
     * 応募情報をIDで1件取得する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     * @return 応募情報。存在しない場合はnull
     */
    public Application findById(String userId, String applicationId) {

        if (!StringUtils.hasText(applicationId)) {
            return null;
        }

        return applicationRepository.findById(userId, applicationId);
    }

    /**
     * ログインユーザーの応募情報を全件取得する
     *
     * @param userId ログインユーザーID
     * @return 応募情報一覧
     */
    public List<Application> findAll(String userId) {

        return applicationRepository.findAll(userId);
    }

    /**
     * 指定企業に紐付く応募情報を取得する
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     * @return 指定企業の応募情報一覧
     */
    public List<ApplicationListItem> findByCompanyId(
            String userId,
            String companyId) {

        // 企業IDの入力を確認する
        if (!StringUtils.hasText(companyId)) {
            throw new IllegalArgumentException(
                    "企業IDが指定されていません。");
        }

        // 指定企業が存在することを確認する
        if (!companyRepository.existsById(userId, companyId)) {
            throw new IllegalArgumentException(
                    "指定された企業情報が見つかりません。");
        }

        return applicationRepository.findByCompanyId(userId, companyId);
    }

    /**
     * 応募情報を削除する
     *
     * @param userId ログインユーザーID
     * @param applicationId 応募情報ID
     */
    public void delete(String userId, String applicationId) {

        // 応募情報IDを確認する
        if (!StringUtils.hasText(applicationId)) {
            throw new IllegalArgumentException(
                    "応募情報IDが指定されていません。");
        }

        // 削除対象の存在を確認する
        if (!applicationRepository.existsById(userId, applicationId)) {
            throw new IllegalArgumentException(
                    "削除対象の応募情報が見つかりません。");
        }

        applicationRepository.deleteById(userId, applicationId);
    }

    /**
     * 応募情報の入力内容を確認・整理する
     *
     * @param userId ログインユーザーID
     * @param application 入力された応募情報
     */
    private void validateAndNormalize(
            String userId,
            Application application) {

        // 応募情報が指定されていることを確認する
        if (application == null) {
            throw new IllegalArgumentException(
                    "応募情報が指定されていません。");
        }

        // 企業IDが入力されていることを確認する
        if (!StringUtils.hasText(application.getCompanyId())) {
            throw new IllegalArgumentException(
                    "関連する企業を指定してください。");
        }

        // 関連付ける企業がログインユーザーの所有する企業か確認する
        if (!companyRepository.existsById(
                userId,
                application.getCompanyId())) {

            throw new IllegalArgumentException(
                    "関連付ける企業情報が見つかりません。");
        }

        // 求人名が入力されている場合は前後の空白を除去する
        if (application.getJobTitle() != null) {
            application.setJobTitle(
                    application.getJobTitle().trim());
        }

        // 応募・選考状況が入力されていることを確認する
        if (!StringUtils.hasText(application.getStatus())) {
            throw new IllegalArgumentException(
                    "応募・選考状況を指定してください。");
        }

        // 応募・選考状況が許可された値か確認する
        List<String> allowedStatuses = List.of(
                "INTERESTED",
                "SCOUT",
                "CASUAL_INTERVIEW",
                "DOCUMENTS",
                "INTERVIEW",
                "ACCEPTED",
                "REJECTED",
                "DECLINED"
        );

        if (!allowedStatuses.contains(application.getStatus())) {
            throw new IllegalArgumentException(
                    "指定された応募・選考状況は使用できません。");
        }

        // メモが入力されている場合は前後の空白を除去する
        if (application.getMemo() != null) {
            application.setMemo(application.getMemo().trim());
        }
    }
}
