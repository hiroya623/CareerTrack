package com.careertrack.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.careertrack.model.Company;
import com.careertrack.dto.CompanyListItem;
import com.careertrack.repository.CompanyRepository;

/**
 * 企業情報に関する業務処理を担当するServiceクラス
 *
 * Controllerから受け取った企業情報を整理・検証し、
 * CompanyRepositoryを通じてFirestoreを操作する。
 */
@Service
public class CompanyService {

    /**
     * 企業情報のRepository
     */
    private final CompanyRepository companyRepository;

    /**
     * コンストラクタ
     *
     * @param companyRepository 企業情報のRepository
     */
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * 企業情報を新規登録する
     *
     * @param userId ログインユーザーID
     * @param company 登録する企業情報
     * @return 登録された企業ID
     */
    public String register(String userId, Company company) {

        // 企業名の入力チェックと前後の空白除去
        validateAndNormalize(company);

        // 登録日時と更新日時を設定する
        Date now = new Date();
        company.setCreatedAt(now);
        company.setUpdatedAt(now);

        // Repositoryを通じてFirestoreに保存する
        return companyRepository.save(userId, null, company);
    }

    /**
     * 既存の企業情報を更新する
     *
     * @param userId ログインユーザーID
     * @param companyId 更新する企業ID
     * @param company 更新後の企業情報
     * @return 更新された企業ID
     */
    public String update(String userId, String companyId, Company company) {

        // 企業IDの入力チェック
        if (!StringUtils.hasText(companyId)) {
            throw new IllegalArgumentException(
                    "企業IDが指定されていません。");
        }

        // 自分の企業情報として存在するか確認する
        Company existingCompany = companyRepository.findById(
                userId, companyId);

        if (existingCompany == null) {
            throw new IllegalArgumentException(
                    "更新対象の企業情報が見つかりません。");
        }

        // 企業名の入力チェックと前後の空白除去
        validateAndNormalize(company);

        // 登録日時は元の値を引き継ぎ、更新日時だけ変更する
        company.setCreatedAt(existingCompany.getCreatedAt());
        company.setUpdatedAt(new Date());

        // Repositoryを通じてFirestoreを更新する
        return companyRepository.save(userId, companyId, company);
    }

    /**
     * 企業IDを指定して企業情報を取得する
     *
     * @param userId ログインユーザーID
     * @param companyId 企業ID
     * @return 企業情報
     */
    public Company findById(String userId, String companyId) {

        return companyRepository.findById(userId, companyId);
    }

    
    /**
     * ログインユーザーの企業情報をすべて取得する
     *
     * 企業一覧画面で使用するため、
     * FirestoreのドキュメントIDを含むDTOの一覧を取得する。
     *
     * @param userId ログインユーザーID
     * @return 企業IDを含む企業情報の一覧
     */
    public List<CompanyListItem> findAll(String userId) {

        // Repositoryから企業ID付きの企業一覧を取得する
        return companyRepository.findAll(userId);
    }


    /**
     * 企業情報を削除する
     *
     * @param userId ログインユーザーID
     * @param companyId 削除する企業ID
     */
    public void delete(String userId, String companyId) {

        // 企業IDの入力チェック
        if (!StringUtils.hasText(companyId)) {
            throw new IllegalArgumentException(
                    "企業IDが指定されていません。");
        }

        // 削除対象の企業情報が存在するか確認する
        if (!companyRepository.existsById(userId, companyId)) {
            throw new IllegalArgumentException(
                    "削除対象の企業情報が見つかりません。");
        }

        // Repositoryを通じてFirestoreから削除する
        companyRepository.deleteById(userId, companyId);
    }

    /**
     * 企業情報の入力値を検証・整理する
     *
     * @param company 検証する企業情報
     */
    private void validateAndNormalize(Company company) {

        // 企業情報そのものがnullでないか確認する
        if (company == null) {
            throw new IllegalArgumentException(
                    "企業情報が指定されていません。");
        }

        // 企業名の必須チェック
        if (!StringUtils.hasText(company.getCompanyName())) {
            throw new IllegalArgumentException(
                    "企業名を入力してください。");
        }

        // 企業名の前後の空白を除去する
        company.setCompanyName(company.getCompanyName().trim());
    }
}
