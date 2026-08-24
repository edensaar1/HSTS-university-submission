-- Final consolidated HSTS schema.
-- Run this file first on a MySQL 8 server. It creates the database and every
-- table required by the application, including the approval and grading fields
-- that were originally introduced through migration scripts.

CREATE DATABASE IF NOT EXISTS hsts
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hsts;

CREATE TABLE IF NOT EXISTS Authorized_Users (
    user_id VARCHAR(20) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Teachers (
    user_id VARCHAR(20) PRIMARY KEY,
    CONSTRAINT fk_teachers_user
        FOREIGN KEY (user_id) REFERENCES Authorized_Users(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Students (
    user_id VARCHAR(20) PRIMARY KEY,
    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id) REFERENCES Authorized_Users(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Subject_Coordinators (
    user_id VARCHAR(20) PRIMARY KEY,
    CONSTRAINT fk_coordinators_user
        FOREIGN KEY (user_id) REFERENCES Authorized_Users(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS subjects (
    subject_id VARCHAR(20) PRIMARY KEY,
    subject_name VARCHAR(150) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS courses (
    course_id VARCHAR(20) PRIMARY KEY,
    subject_id VARCHAR(20) NOT NULL,
    coordinator_id VARCHAR(20) NOT NULL,
    course_name VARCHAR(150) NOT NULL,
    CONSTRAINT fk_courses_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    CONSTRAINT fk_courses_coordinator
        FOREIGN KEY (coordinator_id) REFERENCES Subject_Coordinators(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS teacher_courses (
    teacher_id VARCHAR(20) NOT NULL,
    course_id VARCHAR(20) NOT NULL,
    PRIMARY KEY (teacher_id, course_id),
    CONSTRAINT fk_teacher_courses_teacher
        FOREIGN KEY (teacher_id) REFERENCES Teachers(user_id),
    CONSTRAINT fk_teacher_courses_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_id VARCHAR(20) NOT NULL,
    grade INT NULL,
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id),
    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id) REFERENCES Students(user_id),
    CONSTRAINT fk_enrollments_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS questions (
    question_id VARCHAR(5) PRIMARY KEY,
    course_id VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    answer1 VARCHAR(500) NOT NULL,
    answer2 VARCHAR(500) NOT NULL,
    answer3 VARCHAR(500) NOT NULL,
    answer4 VARCHAR(500) NOT NULL,
    correct_answer INT NOT NULL,
    illustrationPath VARCHAR(500) NULL,
    CONSTRAINT chk_questions_correct_answer CHECK (correct_answer BETWEEN 1 AND 4),
    CONSTRAINT fk_questions_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS exams (
    exam_id VARCHAR(6) PRIMARY KEY,
    teacher_id VARCHAR(20) NOT NULL,
    course_id VARCHAR(20) NOT NULL,
    duration INT NOT NULL,
    student_Instructions TEXT NULL,
    teacher_Instructions TEXT NULL,
    status VARCHAR(30) NOT NULL,
    rejectionReason VARCHAR(500) NULL,
    CONSTRAINT chk_exams_duration CHECK (duration > 0),
    CONSTRAINT fk_exams_teacher
        FOREIGN KEY (teacher_id) REFERENCES Teachers(user_id),
    CONSTRAINT fk_exams_course
        FOREIGN KEY (course_id) REFERENCES courses(course_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS exam_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id VARCHAR(6) NOT NULL,
    question_id VARCHAR(5) NOT NULL,
    points INT NOT NULL,
    CONSTRAINT uq_exam_question UNIQUE (exam_id, question_id),
    CONSTRAINT chk_exam_questions_points CHECK (points > 0),
    CONSTRAINT fk_exam_questions_exam
        FOREIGN KEY (exam_id) REFERENCES exams(exam_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_exam_questions_question
        FOREIGN KEY (question_id) REFERENCES questions(question_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS exam_instances (
    instanceId BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id VARCHAR(6) NOT NULL,
    administering_teacher_id VARCHAR(20) NOT NULL,
    openingTime DATETIME NOT NULL,
    closingTime DATETIME NOT NULL,
    executionCode VARCHAR(4) NOT NULL,
    extraTimeMinutes INT NOT NULL DEFAULT 0,
    INDEX idx_exam_instances_execution_code (executionCode),
    CONSTRAINT chk_exam_instances_schedule CHECK (closingTime > openingTime),
    CONSTRAINT chk_exam_instances_extra_time CHECK (extraTimeMinutes >= 0),
    CONSTRAINT fk_exam_instances_exam
        FOREIGN KEY (exam_id) REFERENCES exams(exam_id),
    CONSTRAINT fk_exam_instances_teacher
        FOREIGN KEY (administering_teacher_id) REFERENCES Teachers(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS exam_submissions (
    submissionId BIGINT AUTO_INCREMENT PRIMARY KEY,
    instance_id BIGINT NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    startedAt DATETIME NOT NULL,
    submittedAt DATETIME NULL,
    status VARCHAR(30) NOT NULL,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    actualDurationMinutes INT NOT NULL DEFAULT 0,
    automaticGrade INT NULL,
    finalGrade INT NULL,
    teacherComment VARCHAR(1000) NULL,
    gradeChangeReason VARCHAR(1000) NULL,
    CONSTRAINT uq_exam_submission UNIQUE (instance_id, student_id),
    CONSTRAINT chk_exam_submission_duration CHECK (actualDurationMinutes >= 0),
    CONSTRAINT chk_exam_submission_automatic_grade CHECK (automaticGrade BETWEEN 0 AND 100),
    CONSTRAINT chk_exam_submission_final_grade CHECK (finalGrade BETWEEN 0 AND 100),
    CONSTRAINT fk_exam_submissions_instance
        FOREIGN KEY (instance_id) REFERENCES exam_instances(instanceId),
    CONSTRAINT fk_exam_submissions_student
        FOREIGN KEY (student_id) REFERENCES Students(user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS submission_answers (
    answerId BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    exam_question_id INT NOT NULL,
    selected_answer INT NOT NULL,
    CONSTRAINT uq_submission_answer UNIQUE (submission_id, exam_question_id),
    CONSTRAINT chk_submission_answer_selection CHECK (selected_answer BETWEEN 1 AND 4),
    CONSTRAINT fk_submission_answers_submission
        FOREIGN KEY (submission_id) REFERENCES exam_submissions(submissionId)
        ON DELETE CASCADE,
    CONSTRAINT fk_submission_answers_exam_question
        FOREIGN KEY (exam_question_id) REFERENCES exam_questions(id)
) ENGINE = InnoDB;
