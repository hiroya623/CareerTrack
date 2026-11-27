
package com.careertrack.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.careertrack.dto.ApplicationListItem;
import com.careertrack.dto.CompanyListItem;
import com.careertrack.dto.TaskListItem;
import com.careertrack.model.Document;
import com.careertrack.model.Interview;

/**
 * タスク一覧に表示する情報を取得・集約するServiceクラス。
 *
 * 各企業に登録されている以下の情報を取得する。
 *
 * ・応募・選考情報
 * ・提出書類情報
 * ・面談・面接情報
 *
 * タスク専用のデータはFirestoreに保存しない。
 * 既存の登録情報から画面表示用のDTOを生成する。
 */
@Service
public class TaskService {

    private final CompanyService companyService;
    private final ApplicationService applicationService;
    private final DocumentService documentService;
    private final InterviewService interviewService;

    /**
     * コンストラクタ
     */
    public TaskService(
            CompanyService companyService,
            ApplicationService applicationService,
            DocumentService documentService,
            InterviewService interviewService) {

        this.companyService = companyService;
        this.applicationService = applicationService;
        this.documentService = documentService;
        this.interviewService = interviewService;
    }

    /**
     * ログインユーザーのタスク情報をすべて取得する。
     *
     * @param userId ログインユーザーID
     * @return タスク一覧
     */
    public List<TaskListItem> findAll(String userId) {

        // タスク一覧を格納するリスト
        List<TaskListItem> tasks = new ArrayList<>();

        // 本日の日付を取得
        LocalDate today = LocalDate.now();

        // ログインユーザーの企業一覧を取得
        List<CompanyListItem> companies = companyService.findAll(userId);

        // 企業ごとにタスク情報を取得する
        for (CompanyListItem company : companies) {

            // 応募・選考情報
            addApplicationTasks(userId, company, tasks);

            // 提出書類情報
            addDocumentTasks(userId, company, tasks, today);

            // 面談・面接情報
            addInterviewTasks(userId, company, tasks, today);
        }

        // タスクを対応優先順位順に並び替える
        sortTasks(tasks, today);

        return tasks;
    }

    /**
     * 応募・選考情報をタスク一覧に追加する。
     *
     * 採用・不採用・辞退は表示対象外とする。
     */
    private void addApplicationTasks(
            String userId,
            CompanyListItem company,
            List<TaskListItem> tasks) {

        // 指定企業の応募情報を取得
        List<ApplicationListItem> applications = applicationService.findByCompanyId(
                userId,
                company.getId());

        for (ApplicationListItem application : applications) {

            String status = application.getStatus();

            // 選考状況に応じた表示名を取得
            String title = getApplicationTitle(status);

            // 表示対象外の応募情報は除外する
            if (title == null) {
                continue;
            }

            // 求人名が登録されている場合は表示名に追加する
            if (StringUtils.hasText(application.getJobTitle())) {

                title += "（" + application.getJobTitle() + "）";
            }

            // タスク一覧表示用DTOを生成
            TaskListItem task = new TaskListItem();

            task.setSourceId(application.getId());
            task.setTaskType("APPLICATION");
            task.setCompanyId(company.getId());
            task.setCompanyName(company.getCompanyName());
            task.setTitle(title);
            task.setStatus(status);

            // 応募・選考情報には対応予定日・期限日がないため、
            // 日付は設定しない。

            tasks.add(task);
        }
    }

    /**
     * 未提出の書類情報をタスク一覧に追加する。
     *
     * 1つの書類につき1件のタスクを生成する。
     */
    private void addDocumentTasks(
            String userId,
            CompanyListItem company,
            List<TaskListItem> tasks,
            LocalDate today) {

        // 指定企業の書類情報を取得
        List<Document> documents = documentService.findAll(
                userId,
                company.getId());

        for (Document document : documents) {

            // 未提出の書類のみ表示する
            if (!"NOT_SUBMITTED".equals(
                    document.getSubmissionStatus())) {

                continue;
            }

            // 提出予定日と提出期限日を取得
            Date plannedDate = document.getPlannedSubmissionDate();

            Date deadlineDate = document.getDeadlineDate();

            // タスク一覧表示用DTOを生成
            TaskListItem task = new TaskListItem();

            task.setSourceId(document.getId());
            task.setTaskType("DOCUMENT");
            task.setCompanyId(company.getId());
            task.setCompanyName(company.getCompanyName());

            task.setTitle(
                    document.getDocumentName() + "の提出");

            task.setStatus(document.getSubmissionStatus());
            task.setPlannedDate(plannedDate);
            task.setDeadlineDate(deadlineDate);

            // 提出期限を超過しているか確認
            if (deadlineDate != null) {

                task.setDeadlineOverdue(
                        toLocalDate(deadlineDate)
                                .isBefore(today));
            }

            // 提出予定日を超過しているか確認
            if (plannedDate != null) {

                task.setPlannedDateOverdue(
                        toLocalDate(plannedDate)
                                .isBefore(today));
            }

            tasks.add(task);
        }
    }

    /**
     * 本日以降の面談・面接情報をタスク一覧に追加する。
     *
     * 昨日以前の面談・面接は表示対象外とする。
     */
    private void addInterviewTasks(
            String userId,
            CompanyListItem company,
            List<TaskListItem> tasks,
            LocalDate today) {

        // 指定企業の面談・面接情報を取得
        List<Interview> interviews = interviewService.findAll(
                userId,
                company.getId());

        for (Interview interview : interviews) {

            // 面談・面接予定日時を取得
            Date interviewDateTime = interview.getInterviewDateTime();

            // 日時が設定されていない場合は除外
            if (interviewDateTime == null) {
                continue;
            }

            // 昨日以前の面談・面接は除外
            if (toLocalDate(interviewDateTime)
                    .isBefore(today)) {

                continue;
            }

            // タスク一覧表示用DTOを生成
            TaskListItem task = new TaskListItem();

            task.setSourceId(interview.getId());
            task.setTaskType("INTERVIEW");
            task.setCompanyId(company.getId());
            task.setCompanyName(company.getCompanyName());
            task.setTitle(interview.getInterviewName());

            // 対面・オンラインの実施形式を設定
            task.setStatus(interview.getInterviewFormat());

            // 面談・面接の実施予定日時を設定
            task.setPlannedDate(interviewDateTime);

            tasks.add(task);
        }
    }

    /**
     * 応募・選考状況に応じたタスクの表示名を取得する。
     *
     * 表示対象外の場合はnullを返す。
     */
    private String getApplicationTitle(String status) {

        if (status == null) {
            return null;
        }

        return switch (status) {

            case "INTERESTED" -> "応募検討";

            case "SCOUT" -> "スカウト対応";

            case "CASUAL_INTERVIEW" -> "カジュアル面談選考状況";

            case "DOCUMENTS" -> "書類選考状況";

            case "INTERVIEW" -> "面接選考状況";

            default -> null;
        };
    }

    /**
     * タスク一覧を対応優先順位順に並び替える。
     *
     * 1. 期限超過
     * 2. 予定日超過
     * 3. 本日の予定・期限
     * 4. 今後の予定・期限
     * 5. 日付未設定
     */
    private void sortTasks(
            List<TaskListItem> tasks,
            LocalDate today) {

        tasks.sort(
                Comparator
                        .comparingInt(
                                (TaskListItem task) -> getPriorityOrder(task, today))

                        // 同じ優先順位の場合は日付順
                        .thenComparing(
                                this::getSortDate,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))

                        // 同じ日付の場合は企業名順
                        .thenComparing(
                                TaskListItem::getCompanyName,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))

                        // 同じ企業の場合はタイトル順
                        .thenComparing(
                                TaskListItem::getTitle,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder())));
    }

    /**
     * タスクの表示優先順位を取得する。
     *
     * 数値が小さいほど優先順位が高い。
     */
    private int getPriorityOrder(
            TaskListItem task,
            LocalDate today) {

        // 提出期限超過
        if (task.isDeadlineOverdue()) {
            return 0;
        }

        // 提出予定日超過
        if (task.isPlannedDateOverdue()) {
            return 1;
        }

        // 並び替えに使用する日付を取得
        Date sortDate = getSortDate(task);

        // 日付未設定
        if (sortDate == null) {
            return 4;
        }

        // 本日の予定・期限
        if (toLocalDate(sortDate).equals(today)) {
            return 2;
        }

        // 今後の予定・期限
        return 3;
    }

    /**
     * 並び替えに使用する日付を取得する。
     *
     * 期限超過の場合は期限日を使用する。
     * それ以外の場合は予定日と期限日のうち早い方を使用する。
     */
    private Date getSortDate(TaskListItem task) {

        Date plannedDate = task.getPlannedDate();
        Date deadlineDate = task.getDeadlineDate();

        // 期限超過の場合は期限日を優先する
        if (task.isDeadlineOverdue()) {
            return deadlineDate;
        }

        // 予定日が未設定の場合
        if (plannedDate == null) {
            return deadlineDate;
        }

        // 期限日が未設定の場合
        if (deadlineDate == null) {
            return plannedDate;
        }

        // 両方設定されている場合は早い方を返す
        return plannedDate.before(deadlineDate)
                ? plannedDate
                : deadlineDate;
    }

    /**
     * Date型をLocalDate型に変換する。
     *
     * 時刻を除外して日付単位で比較するために使用する。
     */
    private LocalDate toLocalDate(Date date) {

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
