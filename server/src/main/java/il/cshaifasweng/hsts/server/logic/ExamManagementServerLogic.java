package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.Course;
import il.cshaifasweng.hsts.entities.CurrentSession;
import il.cshaifasweng.hsts.entities.Exam;
import il.cshaifasweng.hsts.entities.ExamInstance;
import il.cshaifasweng.hsts.entities.ExamQuestion;
import il.cshaifasweng.hsts.entities.Teacher;
import il.cshaifasweng.hsts.entities.enums.ExamStatus;
import il.cshaifasweng.hsts.entities.view.ExamInstanceView;
import il.cshaifasweng.hsts.entities.view.ExamQuestionView;
import il.cshaifasweng.hsts.entities.view.ExamView;
import il.cshaifasweng.hsts.server.repositories.ExamRepository;
import il.cshaifasweng.hsts.server.repositories.ExaminationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class  ExamManagementServerLogic {
    private final ExamRepository examRepository;
    private final ExaminationRepository examinationRepository;

    public ExamManagementServerLogic(
            ExamRepository examRepository,
            ExaminationRepository examinationRepository) {
        this.examRepository = examRepository;
        this.examinationRepository = examinationRepository;
    }

    public List<ExamView> getApprovedExams(CurrentSession currentSession) {
        Teacher teacher = requireTeacher(currentSession);
        List<Exam> approvedExams =
                examRepository.getApprovedExamsForTeacher(teacher.getUserId());
        List<ExamView> examViews = new ArrayList<>();

        for (Exam exam : approvedExams) {
            examViews.add(toExamView(exam));
        }
        return examViews;
    }

    public boolean scheduleExam(
            CurrentSession currentSession,
            ExamInstanceView instanceView) {
        Teacher teacher = requireTeacher(currentSession);

        if (instanceView == null) {
            throw new IllegalArgumentException("Exam schedule is missing");
        }
        if (instanceView.getExamId() == null || instanceView.getExamId().isBlank()) {
            throw new IllegalArgumentException("Exam ID is missing");
        }

        LocalDateTime openingTime = instanceView.getOpeningTime();
        LocalDateTime closingTime = instanceView.getClosingTime();
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times are required");
        }
        if (!closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
        LocalDateTime currentMinute =
                LocalDateTime.now().withSecond(0).withNano(0);
        if (openingTime.isBefore(currentMinute)) {
            throw new IllegalArgumentException("Opening time cannot be in the past");
        }
        if (!closingTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Closing time must be in the future");
        }

        String executionCode = instanceView.getExecutionCode();
        if (executionCode == null || executionCode.isBlank()) {
            throw new IllegalArgumentException("Execution code is required");
        }
        String normalizedCode = executionCode.trim().toUpperCase(Locale.ROOT);
        if (!normalizedCode.matches("[A-Z0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Execution code must contain exactly four letters or digits");
        }

        Exam exam = examRepository.getExamById(instanceView.getExamId());
        if (exam == null) {
            throw new IllegalArgumentException("Exam does not exist");
        }
        if (exam.getStatus() != ExamStatus.APPROVED) {
            throw new IllegalStateException("Only an approved exam can be scheduled");
        }

        boolean teacherTeachesCourse = false;
        List<Exam> approvedExamsForTeacher =
                examRepository.getApprovedExamsForTeacher(teacher.getUserId());
        for (Exam approvedExam : approvedExamsForTeacher) {
            if (approvedExam.getExamId().equals(exam.getExamId())) {
                teacherTeachesCourse = true;
                break;
            }
        }
        if (!teacherTeachesCourse) {
            throw new IllegalStateException(
                    "Teacher does not teach the exam's course");
        }

        if (examinationRepository.hasOverlappingExecutionCode(
                normalizedCode, openingTime, closingTime)) {
            throw new IllegalStateException(
                    "Execution code is already used during this time window");
        }

        ExamInstance examInstance = new ExamInstance(
                exam,
                teacher,
                openingTime,
                closingTime,
                normalizedCode
        );
        return examinationRepository.saveExamInstance(examInstance);
    }

    public List<ExamInstanceView> getTeacherExamInstances(
            CurrentSession currentSession) {
        Teacher teacher = requireTeacher(currentSession);
        List<ExamInstance> examInstances = examinationRepository
                .getExamInstancesForTeacher(teacher.getUserId());
        List<ExamInstanceView> instanceViews = new ArrayList<>();

        for (ExamInstance examInstance : examInstances) {
            instanceViews.add(toExamInstanceView(examInstance));
        }
        return instanceViews;
    }

    public boolean extendExamDuration(
            CurrentSession currentSession,
            Long instanceId,
            int additionalMinutes) {
        Teacher teacher = requireTeacher(currentSession);

        if (instanceId == null) {
            throw new IllegalArgumentException("Exam instance ID is missing");
        }
        if (additionalMinutes <= 0) {
            throw new IllegalArgumentException("Additional minutes must be positive");
        }

        ExamInstance examInstance =
                examinationRepository.getExamInstanceById(instanceId);
        if (examInstance == null) {
            throw new IllegalArgumentException("Exam instance does not exist");
        }
        if (!examInstance.getAdministeringTeacher().getUserId()
                .equals(teacher.getUserId())) {
            throw new IllegalStateException(
                    "Only the administering teacher can extend this exam");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isBefore(examInstance.getOpeningTime()) ||
                currentTime.isAfter(examInstance.getClosingTime())) {
            throw new IllegalStateException(
                    "Exam duration can only be extended during its active window");
        }

        examInstance.extendDuration(additionalMinutes);
        return examinationRepository.updateExamInstance(examInstance);
    }

    private Teacher requireTeacher(CurrentSession currentSession) {
        if (currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if (!"Teacher".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof Teacher)) {
            throw new IllegalStateException("Only a teacher can manage exams");
        }
        return (Teacher) currentSession.getCurrentUser();
    }

    private ExamView toExamView(Exam exam) {
        Course course = exam.getCourse();
        Teacher author = exam.getTeacher();
        ExamView examView = new ExamView(
                course.getCourseId(),
                course.getCourseName(),
                author.getUserId(),
                author.getFullName(),
                exam.getExamId(),
                exam.getDuration(),
                exam.getStudentInstructions(),
                exam.getTeacherInstructions()
        );
        examView.setStatus(exam.getStatus());
        examView.setRejectionReason(exam.getRejectionReason());

        List<ExamQuestionView> questionViews = new ArrayList<>();
        for (ExamQuestion examQuestion : exam.getExamQuestions()) {
            questionViews.add(new ExamQuestionView(
                    examQuestion.getQuestion().getQuestionId(),
                    examQuestion.getPoints()
            ));
        }
        examView.setExamQuestions(questionViews);
        return examView;
    }

    private ExamInstanceView toExamInstanceView(ExamInstance examInstance) {
        Exam exam = examInstance.getExam();
        Course course = exam.getCourse();
        Teacher administeringTeacher = examInstance.getAdministeringTeacher();

        ExamInstanceView instanceView = new ExamInstanceView(
                exam.getExamId(),
                examInstance.getOpeningTime(),
                examInstance.getClosingTime(),
                examInstance.getExecutionCode()
        );
        instanceView.setInstanceId(examInstance.getInstanceId());
        instanceView.setCourseId(course.getCourseId());
        instanceView.setCourseName(course.getCourseName());
        instanceView.setAdministeringTeacherId(administeringTeacher.getUserId());
        instanceView.setAdministeringTeacherName(administeringTeacher.getFullName());
        instanceView.setOriginalDuration(exam.getDuration());
        instanceView.setExtraTimeMinutes(examInstance.getExtraTimeMinutes());
        return instanceView;
    }
}
