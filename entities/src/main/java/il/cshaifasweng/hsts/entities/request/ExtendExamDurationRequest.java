package il.cshaifasweng.hsts.entities.request;

import java.io.Serializable;

public class ExtendExamDurationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long instanceId;
    private final int additionalMinutes;

    public ExtendExamDurationRequest(Long instanceId, int additionalMinutes) {
        this.instanceId = instanceId;
        this.additionalMinutes = additionalMinutes;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public int getAdditionalMinutes() {
        return additionalMinutes;
    }
}
