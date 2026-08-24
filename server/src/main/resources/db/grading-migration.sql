USE hsts;

ALTER TABLE exam_submissions
    ADD COLUMN automaticGrade INT NULL,
    ADD COLUMN finalGrade INT NULL,
    ADD COLUMN teacherComment VARCHAR(1000) NULL,
    ADD COLUMN gradeChangeReason VARCHAR(1000) NULL,
    ADD CONSTRAINT chk_exam_submission_automatic_grade CHECK (automaticGrade BETWEEN 0 AND 100),
    ADD CONSTRAINT chk_exam_submission_final_grade CHECK (finalGrade BETWEEN 0 AND 100);
