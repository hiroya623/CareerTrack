
package com.careertrack.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.careertrack.model.Document;
import com.careertrack.repository.CompanyRepository;
import com.careertrack.repository.DocumentRepository;

/**
 * 企業ごとの書類提出情報を管理するServiceクラス。
 *
 * 書類情報の入力チェックや登録・更新・取得・削除を担当する。
 * 書類ファイル本体は扱わず、Firestoreに管理情報のみを保存する。
 */
@Service
public class DocumentService {

    /**
     * 書類情報を操作するRepository
     */
    private final DocumentRepository documentRepository;

    /**
     * 企業情報を操作するRepository
     */
    private final CompanyRepository companyRepository;

    /**
     * コンストラクタ
     *
     * @param documentRepository 書類情報Repository
     * @param companyRepository  企業情報Repository
     */
    public DocumentService(
            DocumentRepository documentRepository,
            CompanyRepository companyRepository) {

        this.documentRepository = documentRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 書類情報を新規登録する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     * @param document  登録する書類情報
     * @return 登録した書類ID
     */
    public String register(
            String userId,
            String companyId,
            Document document) {

        // 企業の存在を確認する
        validateCompany(userId, companyId);

        // 書類情報の入力内容を確認する
        validateDocument(document);

        // 登録日時と更新日時を設定する
        Date now = new Date();

        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        // 新規登録時は未提出を初期値とする
        if (document.getSubmissionStatus() == null
                || document.getSubmissionStatus().isBlank()) {

            document.setSubmissionStatus("NOT_SUBMITTED");
        }

        // 提出状況に応じた日付を確認する
        validateSubmissionDate(document);

        // 書類情報を保存する
        return documentRepository.save(
                userId,
                companyId,
                null,
                document);
    }

    /**
     * 書類情報を更新する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     * @param document   更新する書類情報
     */
    public void update(
            String userId,
            String companyId,
            String documentId,
            Document document) {

        // 企業の存在を確認する
        validateCompany(userId, companyId);

        // 更新対象の書類情報を取得する
        Document existingDocument = findById(
                userId,
                companyId,
                documentId);

        // 書類情報の入力内容を確認する
        validateDocument(document);

        // 元の登録日時を維持する
        document.setCreatedAt(existingDocument.getCreatedAt());

        // 最終更新日時を設定する
        document.setUpdatedAt(new Date());

        // 提出状況に応じた日付を確認する
        validateSubmissionDate(document);

        // 書類情報を更新する
        documentRepository.save(
                userId,
                companyId,
                documentId,
                document);
    }

    /**
     * 書類情報をIDで1件取得する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     * @return 書類情報
     */
    public Document findById(
            String userId,
            String companyId,
            String documentId) {

        // 企業の存在を確認する
        validateCompany(userId, companyId);

        // 書類情報を取得する
        Document document = documentRepository.findById(
                userId,
                companyId,
                documentId);

        if (document == null) {
            throw new IllegalArgumentException(
                    "指定された書類情報が存在しません。");
        }

        return document;
    }

    /**
     * 指定された企業の書類情報をすべて取得する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     * @return 書類情報一覧
     */
    public List<Document> findAll(
            String userId,
            String companyId) {

        // 企業の存在を確認する
        validateCompany(userId, companyId);

        return documentRepository.findAll(userId, companyId);
    }

    /**
     * 書類情報を削除する。
     *
     * @param userId     ログインユーザーID
     * @param companyId  企業ID
     * @param documentId 書類ID
     */
    public void delete(
            String userId,
            String companyId,
            String documentId) {

        // 削除対象の書類情報が存在することを確認する
        findById(userId, companyId, documentId);

        // 書類情報を削除する
        documentRepository.deleteById(
                userId,
                companyId,
                documentId);
    }

    /**
     * 企業情報が存在することを確認する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     */
    private void validateCompany(String userId, String companyId) {

        if (companyId == null || companyId.isBlank()
                || !companyRepository.existsById(userId, companyId)) {

            throw new IllegalArgumentException(
                    "指定された企業情報が存在しません。");
        }
    }

    /**
     * 書類情報の入力内容を確認する。
     *
     * @param document 確認する書類情報
     */
    private void validateDocument(Document document) {

        if (document == null) {
            throw new IllegalArgumentException(
                    "書類情報を入力してください。");
        }

        if (document.getDocumentName() == null
                || document.getDocumentName().isBlank()) {

            throw new IllegalArgumentException(
                    "書類名を入力してください。");
        }

        String status = document.getSubmissionStatus();

        if (status != null && !status.isBlank()
                && !status.equals("NOT_SUBMITTED")
                && !status.equals("SUBMITTED")) {

            throw new IllegalArgumentException(
                    "提出状況の指定が正しくありません。");
        }
    }

    /**
     * 提出状況に応じた日付を確認する。
     *
     * 提出済みの場合は実際の提出日が必須となる。
     * 未提出の場合は実際の提出日をクリアする。
     *
     * @param document 確認する書類情報
     */
    private void validateSubmissionDate(Document document) {

        if ("SUBMITTED".equals(document.getSubmissionStatus())) {

            if (document.getSubmittedDate() == null) {
                throw new IllegalArgumentException(
                        "提出済みの場合は実際の提出日を入力してください。");
            }

        } else {

            document.setSubmittedDate(null);
        }
    }
}
