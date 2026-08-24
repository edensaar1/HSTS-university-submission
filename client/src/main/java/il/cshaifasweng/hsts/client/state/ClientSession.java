package il.cshaifasweng.hsts.client.state;

import il.cshaifasweng.hsts.entities.view.CurrentSessionView;
import il.cshaifasweng.hsts.entities.view.ExamExecutionView;

public final class ClientSession {
    private static CurrentSessionView currentSession;
    private static ExamExecutionView activeExam;

    private ClientSession() {
    }

    public static CurrentSessionView getCurrentSession() {
        return currentSession;
    }

    public static void setCurrentSession(CurrentSessionView currentSession) {
        ClientSession.currentSession = currentSession;
    }

    public static ExamExecutionView getActiveExam() {
        return activeExam;
    }

    public static void setActiveExam(ExamExecutionView activeExam) {
        ClientSession.activeExam = activeExam;
    }

    public static boolean isLoggedIn() {
        return currentSession != null;
    }

    public static void clear() {
        currentSession = null;
        activeExam = null;
    }
}
