package il.cshaifasweng.hsts.entities.request;

import java.io.Serializable;

public class ApproveSubmissionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long submissionId;
    private final int finalGrade;
    private final String teacherComment;
    private final String gradeChangeReason;

    public ApproveSubmissionRequest(Long submissionId, int finalGrade, String teacherComment,
                                    String gradeChangeReason) {
        this.submissionId = submissionId;
        this.finalGrade = finalGrade;
        this.teacherComment = teacherComment;
        this.gradeChangeReason = gradeChangeReason;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public int getFinalGrade() {
        return finalGrade;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public String getGradeChangeReason() {
        return gradeChangeReason;
    }
}
