package com.careertrack.service;

import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.careertrack.model.Interview;
import com.careertrack.repository.InterviewRepository;
import com.careertrack.dto.CompanyListItem;
import com.careertrack.repository.CompanyRepository;

/**
 * 面談・面接情報を管理するServiceクラス。
 *
 * 面談・面接情報の登録・取得・更新・削除などの
 * 業務処理を担当する。
 *
 * また、面談・面接予定同士が前後1時間以内に
 * 重複しないようにチェックする。
 */
@Service
public class InterviewService {

    /**
     * 面談・面接情報を管理するRepository
     */
    private final InterviewRepository interviewRepository;
    /**
     * 企業情報を管理するRepository
     */
    private final CompanyRepository companyRepository;

    /**
     * 面談・面接予定の前後に確保する時間（ミリ秒）。
     *
     * 1時間 = 60分 × 60秒 × 1000ミリ秒
     */
    private static final long INTERVIEW_INTERVAL_MILLIS = 60 * 60 * 1000;

    /**
     * コンストラクタ。
     *
     * @param interviewRepository 面談・面接Repository
     * @param companyRepository   企業情報Repository
     */
    public InterviewService(
            InterviewRepository interviewRepository,
            CompanyRepository companyRepository) {

        this.interviewRepository = interviewRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 面談・面接情報を新規登録する。
     *
     * 登録前に入力値と既存予定との日時重複を確認する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @param interview 登録する面談・面接情報
     * @return 登録された面談・面接ID
     */
    public String save(String userId, String companyId, Interview interview) {

        // 入力値を検証する
        validateInterview(interview);

        // 既存予定との重複を確認する
        validateInterviewDateTime(userId, companyId, interview);

        Date now = new Date();

        interview.setCreatedAt(now);
        interview.setUpdatedAt(now);

        return interviewRepository.save(userId, companyId, interview);
    }

    /**
     * 指定した企業の面談・面接情報をすべて取得する。
     *
     * 本日、今後、過去の予定の順に並び替える。
     * 本日と今後の予定は日時の昇順、
     * 過去の予定は日時の降順で表示する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @return 並び替え済みの面談・面接情報一覧
     */
    public List<Interview> findAll(String userId, String companyId) {

        // 指定企業の面談・面接情報を取得する
        List<Interview> interviews = interviewRepository.findAll(userId, companyId);

        // 本日の日付を取得する
        LocalDate today = LocalDate.now();

        // 表示優先順位と面談・面接日時で並び替える
        return interviews.stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Interview interview) -> getInterviewPriority(interview, today))
                                .thenComparing(
                                        interview -> getUpcomingSortDate(interview, today),
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()))
                                .thenComparing(
                                        Interview::getInterviewDateTime,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder())))
                .toList();
    }

    /**
     * 面談・面接情報の表示優先順位を取得する。
     *
     * 1：本日の予定
     * 2：明日以降の予定
     * 3：過去の予定
     * 4：日時未設定
     *
     * @param interview 面談・面接情報
     * @param today     本日の日付
     * @return 表示優先順位
     */
    private int getInterviewPriority(
            Interview interview,
            LocalDate today) {

        // 面談・面接日時を取得する
        Date interviewDateTime = interview.getInterviewDateTime();

        // 日時が未設定の場合は最後に表示する
        if (interviewDateTime == null) {
            return 4;
        }

        // 日時から年月日を取得する
        LocalDate interviewDate = interviewDateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 本日の予定
        if (interviewDate.equals(today)) {
            return 1;
        }

        // 明日以降の予定
        if (interviewDate.isAfter(today)) {
            return 2;
        }

        // 過去の予定
        return 3;
    }

    /**
     * 本日および今後の予定の並び替えに使用する日時を取得する。
     *
     * 過去の予定は日時の降順で並び替えるため、
     * このメソッドではnullを返す。
     *
     * @param interview 面談・面接情報
     * @param today     本日の日付
     * @return 並び替えに使用する日時
     */
    private Date getUpcomingSortDate(
            Interview interview,
            LocalDate today) {

        // 表示優先順位を取得する
        int priority = getInterviewPriority(interview, today);

        // 本日または明日以降の予定の場合
        if (priority == 1 || priority == 2) {
            return interview.getInterviewDateTime();
        }

        // 過去の予定または日時未設定の場合
        return null;
    }

    /**
     * 指定した面談・面接情報を取得する。
     *
     * @param userId      ユーザーID
     * @param companyId   企業ID
     * @param interviewId 面談・面接ID
     * @return 面談・面接情報
     */
    public Interview findById(String userId, String companyId, String interviewId) {

        return interviewRepository.findById(userId, companyId, interviewId);
    }

    /**
     * 面談・面接情報を更新する。
     *
     * 更新対象自身は重複チェックから除外する。
     *
     * @param userId    ユーザーID
     * @param companyId 企業ID
     * @param interview 更新する面談・面接情報
     */
    public void update(String userId, String companyId, Interview interview) {

        // 更新前の情報を取得する
        Interview existingInterview = interviewRepository.findById(
                userId,
                companyId,
                interview.getId());

        if (existingInterview == null) {
            throw new IllegalArgumentException(
                    "指定された面談・面接情報が見つかりません。");
        }

        // 入力値を検証する
        validateInterview(interview);

        // 元の登録日時を引き継ぐ
        interview.setCreatedAt(existingInterview.getCreatedAt());

        // 更新日時を設定する
        interview.setUpdatedAt(new Date());

        // 更新対象自身を除外して重複チェックする
        validateInterviewDateTime(userId, companyId, interview);

        interviewRepository.update(userId, companyId, interview);
    }

    /**
     * 面談・面接情報を削除する。
     *
     * @param userId      ユーザーID
     * @param companyId   企業ID
     * @param interviewId 面談・面接ID
     */
    public void delete(String userId, String companyId, String interviewId) {

        interviewRepository.delete(userId, companyId, interviewId);
    }

    /**
     * 面談・面接情報の入力値を検証する。
     *
     * @param interview 検証対象の面談・面接情報
     */
    private void validateInterview(Interview interview) {

        // 面談・面接情報そのものがnullでないか確認する
        if (interview == null) {
            throw new IllegalArgumentException(
                    "面談・面接情報が指定されていません。");
        }

        // 面談・面接名の必須チェック
        if (!StringUtils.hasText(interview.getInterviewName())) {
            throw new IllegalArgumentException(
                    "面談・面接名を入力してください。");
        }

        // 面談・面接日時の必須チェック
        if (interview.getInterviewDateTime() == null) {
            throw new IllegalArgumentException(
                    "面談・面接日時を入力してください。");
        }

        // 実施方法の必須チェック
        if (!StringUtils.hasText(interview.getInterviewFormat())) {
            throw new IllegalArgumentException(
                    "実施方法を選択してください。");
        }

        // 実施方法が対面の場合
        if ("ONSITE".equals(interview.getInterviewFormat())) {

            // 場所の必須チェック
            if (!StringUtils.hasText(interview.getLocation())) {
                throw new IllegalArgumentException(
                        "対面の場合は実施場所を入力してください。");
            }

            // 実施方法がオンラインの場合
        } else if ("ONLINE".equals(interview.getInterviewFormat())) {

            // オンラインURLの必須チェック
            if (!StringUtils.hasText(interview.getOnlineUrl())) {
                throw new IllegalArgumentException(
                        "オンラインの場合は接続URLを入力してください。");
            }

            // URLの形式を確認する
            String onlineUrl = interview.getOnlineUrl().trim();

            if (!onlineUrl.matches("^https?://.+")) {
                throw new IllegalArgumentException(
                        "オンラインURLはhttp://またはhttps://から入力してください。");
            }

            interview.setOnlineUrl(onlineUrl);

        } else {

            // 対応していない実施方法の場合
            throw new IllegalArgumentException(
                    "正しい実施方法を選択してください。");
        }

        // 入力文字列の前後の空白を除去する
        interview.setInterviewName(interview.getInterviewName().trim());

        if (interview.getLocation() != null) {
            interview.setLocation(interview.getLocation().trim());
        }

        if (interview.getAccessInfo() != null) {
            interview.setAccessInfo(interview.getAccessInfo().trim());
        }

        if (interview.getMemo() != null) {
            interview.setMemo(interview.getMemo().trim());
        }
    }

    /**
     * 面談・面接日時がログインユーザーの既存予定と
     * 重複しないか確認する。
     *
     * すべての企業の面談・面接情報を確認し、
     * 前後1時間以内に別の予定がある場合は登録・更新を禁止する。
     *
     * @param userId    ユーザーID
     * @param companyId 登録・更新対象の企業ID
     * @param interview 登録・更新対象の面談・面接情報
     */
    private void validateInterviewDateTime(
            String userId,
            String companyId,
            Interview interview) {

        Date targetDateTime = interview.getInterviewDateTime();

        // 日時が入力されていない場合はチェックしない
        if (targetDateTime == null) {
            return;
        }

        // ログインユーザーが登録している企業一覧を取得する
        List<CompanyListItem> companies = companyRepository.findAll(userId);

        // 登録・更新対象の日時をミリ秒に変換する
        long targetTime = targetDateTime.getTime();

        // すべての企業の面談・面接情報を確認する
        for (CompanyListItem company : companies) {

            // 対象企業の面談・面接情報を取得する
            List<Interview> interviews = interviewRepository.findAll(
                    userId,
                    company.getId());

            for (Interview existingInterview : interviews) {

                // 更新対象自身はチェック対象から除外する
                if (companyId.equals(company.getId())
                        && interview.getId() != null
                        && interview.getId().equals(existingInterview.getId())) {
                    continue;
                }

                Date existingDateTime = existingInterview.getInterviewDateTime();

                // 日時が設定されていないデータは除外する
                if (existingDateTime == null) {
                    continue;
                }

                // 既存予定との時間差を計算する
                long timeDifference = Math.abs(
                        targetTime - existingDateTime.getTime());

                // 前後1時間以内の場合は例外を発生させる
                if (timeDifference <= INTERVIEW_INTERVAL_MILLIS) {
                    throw new IllegalArgumentException(
                            "既に別の企業を含む面談・面接予定が前後1時間以内に登録されています。");
                }
            }
        }
    }
}