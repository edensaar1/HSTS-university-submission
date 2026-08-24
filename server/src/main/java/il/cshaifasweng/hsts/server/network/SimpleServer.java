package il.cshaifasweng.hsts.server.network;

import il.cshaifasweng.hsts.entities.CurrentSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.ApproveSubmissionRequest;
import il.cshaifasweng.hsts.entities.request.ExtendExamDurationRequest;
import il.cshaifasweng.hsts.entities.request.LoginRequest;
import il.cshaifasweng.hsts.entities.request.RejectExamRequest;
import il.cshaifasweng.hsts.entities.request.StartExamRequest;
import il.cshaifasweng.hsts.entities.request.SubmitExamRequest;
import il.cshaifasweng.hsts.entities.view.CourseView;
import il.cshaifasweng.hsts.entities.view.CurrentSessionView;
import il.cshaifasweng.hsts.entities.view.ExamExecutionView;
import il.cshaifasweng.hsts.entities.view.ExamInstanceView;
import il.cshaifasweng.hsts.entities.view.ExamSubmissionView;
import il.cshaifasweng.hsts.entities.view.ExamStatisticsView;
import il.cshaifasweng.hsts.entities.view.StudentResultView;
import il.cshaifasweng.hsts.entities.view.TeacherQuestionView;
import il.cshaifasweng.hsts.server.logic.ExamApprovalServerLogic;
import il.cshaifasweng.hsts.server.logic.ExamManagementServerLogic;
import il.cshaifasweng.hsts.server.logic.ExamExecutionServerLogic;
import il.cshaifasweng.hsts.server.logic.ExamSubmissionManagementServerLogic;
import il.cshaifasweng.hsts.server.logic.LoginServerLogic;
import il.cshaifasweng.hsts.server.logic.QuestionBankServerLogic;
import il.cshaifasweng.hsts.server.logic.StudentResultsServerLogic;
import il.cshaifasweng.hsts.server.logic.TeacherStatisticsServerLogic;
import il.cshaifasweng.hsts.server.ocsf.AbstractServer;
import il.cshaifasweng.hsts.server.ocsf.ConnectionToClient;
import il.cshaifasweng.hsts.server.repositories.CourseRepository;
import il.cshaifasweng.hsts.server.repositories.QuestionRepository;
import il.cshaifasweng.hsts.server.repositories.UserRepository;
import il.cshaifasweng.hsts.entities.view.ExamView;
import il.cshaifasweng.hsts.server.logic.ExamCreationServerLogic;
import il.cshaifasweng.hsts.server.repositories.ExamRepository;
import il.cshaifasweng.hsts.server.repositories.ExaminationRepository;
import il.cshaifasweng.hsts.server.repositories.ExamSubmissionsRepository;
import il.cshaifasweng.hsts.server.repositories.StatisticsRepository;

import java.io.IOException;
import java.util.List;

public class SimpleServer extends AbstractServer {
    private static final String SESSION_KEY = "currentSession";

    private final LoginServerLogic loginServerLogic;
    private final QuestionBankServerLogic questionBankServerLogic;
    private final ExamCreationServerLogic examCreationServerLogic;
    private final ExamApprovalServerLogic examApprovalServerLogic;
    private final ExamManagementServerLogic examManagementServerLogic;
    private final ExamExecutionServerLogic examExecutionServerLogic;
    private final ExamSubmissionManagementServerLogic examSubmissionManagementServerLogic;
    private final StudentResultsServerLogic studentResultsServerLogic;
    private final TeacherStatisticsServerLogic teacherStatisticsServerLogic;

    public SimpleServer(int port) {
        super(port);
        UserRepository userRepository = new UserRepository();
        QuestionRepository questionRepository = new QuestionRepository();
        CourseRepository courseRepository = new CourseRepository();
        ExamRepository examRepository = new ExamRepository();
        ExaminationRepository examinationRepository = new ExaminationRepository();
        ExamSubmissionsRepository examSubmissionsRepository = new ExamSubmissionsRepository();
        StatisticsRepository statisticsRepository = new StatisticsRepository();
        loginServerLogic = new LoginServerLogic(userRepository);
        questionBankServerLogic = new QuestionBankServerLogic(questionRepository, courseRepository);
        examCreationServerLogic = new ExamCreationServerLogic(courseRepository, questionRepository, examRepository);
        examApprovalServerLogic = new ExamApprovalServerLogic(examRepository);
        examManagementServerLogic = new ExamManagementServerLogic(examRepository, examinationRepository);
        examExecutionServerLogic = new ExamExecutionServerLogic(courseRepository, examinationRepository, examSubmissionsRepository);
        examSubmissionManagementServerLogic = new ExamSubmissionManagementServerLogic(
                examRepository, examinationRepository, examSubmissionsRepository);
        studentResultsServerLogic = new StudentResultsServerLogic(examSubmissionsRepository);
        teacherStatisticsServerLogic = new TeacherStatisticsServerLogic(statisticsRepository);

    }

    @Override
    protected void handleMessageFromClient(Object message, ConnectionToClient client) {
        if (!(message instanceof HSTSMessage)) {
            return;
        }

        HSTSMessage request = (HSTSMessage) message;
        HSTSMessage response;

        if (request.getType() == null) {
            response = new HSTSMessage(MessageType.ERROR, "Message type is missing");
        } else {
            try {
                //CHECK IF THIS CONNECTION IS ALREADY LOGGED-IN//
                CurrentSession currentSession = (CurrentSession) client.getInfo(SESSION_KEY);

                switch (request.getType()) {
                    case LOGIN_REQUEST:
                        //IF ITS ALREADY LOGGED IN - EXECPTION!//
                        if (currentSession != null) {
                            throw new IllegalStateException("This connection is already logged in");
                        }
                        //ELSE, WE authenticate credentials and keep the connection info//
                        LoginRequest loginRequest = (LoginRequest) request.getPayload();
                        CurrentSession authenticatedSession = loginServerLogic.login(loginRequest.getUsername(), loginRequest.getPassword());
                        client.setInfo(SESSION_KEY, authenticatedSession);
                        CurrentSessionView sessionView = new CurrentSessionView(authenticatedSession.getUserId(),
                                authenticatedSession.getFullName(), authenticatedSession.getRole());
                        response = new HSTSMessage(MessageType.LOGIN_RESPONSE, sessionView);
                        break;

                    case LOGOUT_REQUEST:
                        if(currentSession == null){
                            throw new IllegalStateException("User is not logged in");
                        }
                        loginServerLogic.logout(currentSession);
                        client.setInfo(SESSION_KEY, null);
                        response = new HSTSMessage(MessageType.LOGOUT_RESPONSE, null);
                        break;


                    case GET_TEACHER_COURSES_REQUEST:
                        List<CourseView> teacherCourses = questionBankServerLogic.getTeacherCourses(currentSession);
                        response = new HSTSMessage(MessageType.GET_TEACHER_COURSES_RESPONSE, teacherCourses);
                        break;

                    case GET_COURSE_QUESTIONS_REQUEST:
                        List<TeacherQuestionView> courseQuestions = questionBankServerLogic.getCourseQuestions(
                                currentSession, (String) request.getPayload());
                        response = new HSTSMessage(MessageType.GET_COURSE_QUESTIONS_RESPONSE, courseQuestions);
                        break;

                    case CREATE_QUESTION_REQUEST:
                        boolean created = questionBankServerLogic.createQuestion(
                                currentSession, (TeacherQuestionView) request.getPayload());
                        response = new HSTSMessage(MessageType.CREATE_QUESTION_RESPONSE, created);
                        break;

                    case UPDATE_QUESTION_REQUEST:
                        boolean updated = questionBankServerLogic.updateQuestion(
                                currentSession, (TeacherQuestionView) request.getPayload());
                        response = new HSTSMessage(MessageType.UPDATE_QUESTION_RESPONSE, updated);
                        break;

                    case DELETE_QUESTION_REQUEST:
                        boolean deleted = questionBankServerLogic.deleteQuestion(
                                currentSession, (String) request.getPayload());
                        response = new HSTSMessage(MessageType.DELETE_QUESTION_RESPONSE, deleted);
                        break;

                    case CREATE_EXAM_REQUEST:
                        boolean createdExam = examCreationServerLogic.createExam(
                                currentSession, (ExamView) request.getPayload());
                        response = new HSTSMessage(MessageType.CREATE_EXAM_RESPONSE, createdExam);
                        break;
                    case GET_PENDING_EXAMS_REQUEST:
                        List<ExamView> examViews = examApprovalServerLogic.getPendingExams(currentSession);
                        response = new HSTSMessage(MessageType.GET_PENDING_EXAMS_RESPONSE, examViews);
                        break;
                    case APPROVE_EXAM_REQUEST:
                        boolean approved = examApprovalServerLogic.approveExam(currentSession,
                                (String) request.getPayload());
                        response = new HSTSMessage(MessageType.APPROVE_EXAM_RESPONSE, approved);
                        break;
                    case REJECT_EXAM_REQUEST:
                        RejectExamRequest rejectExamRequest = (RejectExamRequest) request.getPayload();

                        boolean rejected = examApprovalServerLogic.rejectExam(currentSession,
                                rejectExamRequest.getExamId(), rejectExamRequest.getReason());
                        response = new HSTSMessage(MessageType.REJECT_EXAM_RESPONSE, rejected);
                        break;

                    case GET_APPROVED_EXAMS_REQUEST:
                        List<ExamView> approvedExamViews =
                                examManagementServerLogic.getApprovedExams(currentSession);
                        response = new HSTSMessage(
                                MessageType.GET_APPROVED_EXAMS_RESPONSE, approvedExamViews);
                        break;

                    case SCHEDULE_EXAM_REQUEST:
                        boolean scheduled = examManagementServerLogic.scheduleExam(
                                currentSession, (ExamInstanceView) request.getPayload());
                        response = new HSTSMessage(
                                MessageType.SCHEDULE_EXAM_RESPONSE, scheduled);
                        break;

                    case GET_TEACHER_EXAM_INSTANCES_REQUEST:
                        List<ExamInstanceView> instanceViews =
                                examManagementServerLogic.getTeacherExamInstances(currentSession);
                        response = new HSTSMessage(
                                MessageType.GET_TEACHER_EXAM_INSTANCES_RESPONSE,
                                instanceViews);
                        break;

                    case EXTEND_EXAM_DURATION_REQUEST:
                        ExtendExamDurationRequest extensionRequest =
                                (ExtendExamDurationRequest) request.getPayload();
                        if (extensionRequest == null) {
                            throw new IllegalArgumentException(
                                    "Duration extension request is missing");
                        }
                        boolean extended = examManagementServerLogic.extendExamDuration(
                                currentSession,
                                extensionRequest.getInstanceId(),
                                extensionRequest.getAdditionalMinutes());
                        response = new HSTSMessage(
                                MessageType.EXTEND_EXAM_DURATION_RESPONSE, extended);
                        if(extended){
                            HSTSMessage extensionNotification = new HSTSMessage(
                                    MessageType.EXAM_DURATION_EXTENDED_NOTIFICATION, extensionRequest);
                            try{
                                sendToAllClients(extensionNotification);
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                        break;

                    case START_EXAM_REQUEST:
                        StartExamRequest startExamRequest = (StartExamRequest) request.getPayload();
                        if(startExamRequest == null){
                            throw new IllegalArgumentException("Start exam request is missing");
                        }
                        ExamExecutionView examExecutionView = examExecutionServerLogic.startExam(currentSession,
                                startExamRequest.getExecutionCode(), startExamRequest.getStudentId());
                        response = new HSTSMessage(MessageType.START_EXAM_RESPONSE, examExecutionView);
                        break;

                    case SUBMIT_EXAM_REQUEST:
                        SubmitExamRequest submitExamRequest = (SubmitExamRequest) request.getPayload();
                        if(submitExamRequest == null){
                            throw new IllegalArgumentException("Submit exam request is missing");
                        }
                        boolean submitted = examExecutionServerLogic.submitExam(currentSession,
                                submitExamRequest.getSubmissionId(), submitExamRequest.getAnswers());
                        response = new HSTSMessage(MessageType.SUBMIT_EXAM_RESPONSE, submitted);
                        break;

                    case TIMEOUT_EXAM_REQUEST:
                        SubmitExamRequest timeoutExamRequest = (SubmitExamRequest) request.getPayload();
                        if(timeoutExamRequest == null){
                            throw new IllegalArgumentException("Timeout exam request is missing");
                        }
                        boolean timedOut = examExecutionServerLogic.timeoutExam(currentSession,
                                timeoutExamRequest.getSubmissionId(), timeoutExamRequest.getAnswers());
                        response = new HSTSMessage(MessageType.TIMEOUT_EXAM_RESPONSE, timedOut);
                        break;

                    case GET_SUBMISSIONS_FOR_REVIEW_REQUEST:
                        List<ExamSubmissionView> submissionViews =
                                examSubmissionManagementServerLogic.getSubmissionsForReview(currentSession);
                        response = new HSTSMessage(
                                MessageType.GET_SUBMISSIONS_FOR_REVIEW_RESPONSE, submissionViews);
                        break;

                    case APPROVE_SUBMISSION_REQUEST:
                        ApproveSubmissionRequest approveSubmissionRequest =
                                (ApproveSubmissionRequest) request.getPayload();
                        if(approveSubmissionRequest == null){
                            throw new IllegalArgumentException("Approve submission request is missing");
                        }
                        boolean approvedSubmission = examSubmissionManagementServerLogic.approveSubmission(
                                currentSession, approveSubmissionRequest.getSubmissionId(),
                                approveSubmissionRequest.getFinalGrade(), approveSubmissionRequest.getTeacherComment(),
                                approveSubmissionRequest.getGradeChangeReason());
                        response = new HSTSMessage(
                                MessageType.APPROVE_SUBMISSION_RESPONSE, approvedSubmission);
                        break;

                    case GET_STUDENT_RESULTS_REQUEST:
                        List<StudentResultView> studentResults =
                                studentResultsServerLogic.getStudentResults(currentSession);
                        response = new HSTSMessage(
                                MessageType.GET_STUDENT_RESULTS_RESPONSE, studentResults);
                        break;

                    case GET_TEACHER_STATISTICS_REQUEST:
                        List<ExamStatisticsView> teacherStatistics =
                                teacherStatisticsServerLogic.getTeacherStatistics(currentSession);
                        response = new HSTSMessage(
                                MessageType.GET_TEACHER_STATISTICS_RESPONSE, teacherStatistics);
                        break;

                    default:
                        response = new HSTSMessage(
                                MessageType.ERROR, "Unsupported message type: " + request.getType());
                }
            } catch (RuntimeException e) {
                String errorMessage = e.getMessage() == null ? "Unexpected server error" : e.getMessage();
                response = new HSTSMessage(MessageType.ERROR, errorMessage);
            }
        }

        try {
            client.sendToClient(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void clientDisconnected(ConnectionToClient client){
        CurrentSession currentSession = (CurrentSession) client.getInfo(SESSION_KEY);
        loginServerLogic.logout(currentSession);
        client.setInfo(SESSION_KEY, null);

    }


}
