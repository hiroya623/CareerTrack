
package com.careertrack.service;

import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

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
     * 提出状況や提出期限日、提出予定日に応じて
     * 対応が必要な書類を優先的に表示する。
     *
     * @param userId    ログインユーザーID
     * @param companyId 企業ID
     * @return 並び替え済みの書類情報一覧
     */
    public List<Document> findAll(
            String userId,
            String companyId) {

        // 企業の存在を確認する
        validateCompany(userId, companyId);

        // 指定企業の書類情報を取得する
        List<Document> documents = documentRepository.findAll(userId, companyId);

        // 本日の日付を取得する
        LocalDate today = LocalDate.now();

        // 書類の優先順位と日付で並び替える
        return documents.stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Document document) -> getDocumentPriority(document, today))
                                .thenComparing(
                                        document -> getDocumentSortDate(document, today),
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()))
                                .thenComparing(
                                        Document::getSubmittedDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder())))
                .toList();
    }

    /**
     * 書類情報の表示優先順位を取得する。
     *
     * 1：提出期限超過
     * 2：提出予定日超過
     * 3：未提出・日付設定あり
     * 4：未提出・日付未設定
     * 5：提出済み
     *
     * @param document 書類情報
     * @param today    本日の日付
     * @return 表示優先順位
     */
    private int getDocumentPriority(
            Document document,
            LocalDate today) {

        // 提出済みの書類は未提出の書類より後ろに表示する
        if ("SUBMITTED".equals(document.getSubmissionStatus())) {
            return 5;
        }

        // 提出期限日と提出予定日を取得する
        LocalDate deadlineDate = toLocalDate(document.getDeadlineDate());

        LocalDate plannedDate = toLocalDate(document.getPlannedSubmissionDate());

        // 提出期限日を超過している場合
        if (deadlineDate != null
                && deadlineDate.isBefore(today)) {
            return 1;
        }

        // 提出予定日を超過している場合
        if (plannedDate != null
                && plannedDate.isBefore(today)) {
            return 2;
        }

        // いずれかの日付が設定されている場合
        if (deadlineDate != null || plannedDate != null) {
            return 3;
        }

        // 両方の日付が未設定の場合
        return 4;
    }

    /**
     * 書類情報の並び替えに使用する日付を取得する。
     *
     * 提出期限超過の場合は提出期限日、
     * 提出予定日超過の場合は提出予定日を使用する。
     *
     * それ以外の未提出書類は、
     * 提出期限日と提出予定日の早い方を使用する。
     *
     * @param document 書類情報
     * @param today    本日の日付
     * @return 並び替えに使用する日付
     */
    private LocalDate getDocumentSortDate(
            Document document,
            LocalDate today) {

        // 提出済みの書類は実際の提出日で並び替えるため、
        // このメソッドではnullを返す
        if ("SUBMITTED".equals(document.getSubmissionStatus())) {
            return null;
        }

        LocalDate deadlineDate = toLocalDate(document.getDeadlineDate());

        LocalDate plannedDate = toLocalDate(document.getPlannedSubmissionDate());

        // 提出期限超過の場合は提出期限日を使用する
        if (deadlineDate != null
                && deadlineDate.isBefore(today)) {
            return deadlineDate;
        }

        // 提出予定日超過の場合は提出予定日を使用する
        if (plannedDate != null
                && plannedDate.isBefore(today)) {
            return plannedDate;
        }

        // 提出期限日が未設定の場合
        if (deadlineDate == null) {
            return plannedDate;
        }

        // 提出予定日が未設定の場合
        if (plannedDate == null) {
            return deadlineDate;
        }

        // 両方設定されている場合は早い方の日付を使用する
        return deadlineDate.isBefore(plannedDate)
                ? deadlineDate
                : plannedDate;
    }

    /**
     * Date型の日付をLocalDate型に変換する。
     *
     * 時刻を除外し、年月日だけで比較するために使用する。
     *
     * @param date 変換対象の日付
     * @return 変換後の日付。未設定の場合はnull
     */
    private LocalDate toLocalDate(Date date) {

        if (date == null) {
            return null;
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
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
