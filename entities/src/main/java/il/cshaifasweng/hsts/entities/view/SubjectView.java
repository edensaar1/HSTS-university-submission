package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class SubjectView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectId;
    private String subjectName;

    public SubjectView(String subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
