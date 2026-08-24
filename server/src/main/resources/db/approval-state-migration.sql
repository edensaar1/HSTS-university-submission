USE hsts;

ALTER TABLE exam_submissions
    ADD COLUMN approved BOOLEAN NOT NULL DEFAULT FALSE AFTER status;

UPDATE exam_submissions
SET approved = TRUE,
    status = 'SUBMITTED'
WHERE status = 'APPROVED';

SELECT submissionId, status, approved
FROM exam_submissions
ORDER BY submissionId DESC;
