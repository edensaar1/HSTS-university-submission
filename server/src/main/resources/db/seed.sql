USE hsts;

-- Run this file after schema.sql.
-- Development data representing records supplied by the external systems.
-- Passwords are plain text only because the current login model has no hashing yet.
-- Exams, instances and submissions are intentionally not seeded; create them
-- through the application to test the complete workflow.

INSERT IGNORE INTO Authorized_Users
    (user_id, user_name, password, full_name, role)
VALUES
    ('T001', 'teacher1', 'teacher123', 'Dana Cohen', 'Teacher'),
    ('T002', 'teacher2', 'teacher123', 'Noa Levi', 'Teacher'),
    ('S001', 'student1', 'student123', 'Amit Israel', 'Student'),
    ('S002', 'student2', 'student123', 'Maya David', 'Student'),
    ('C001', 'coordinator1', 'coordinator123', 'Roni Shalev', 'SubjectCoordinator');

INSERT IGNORE INTO Teachers (user_id)
VALUES ('T001'), ('T002');

INSERT IGNORE INTO Students (user_id)
VALUES ('S001'), ('S002');

INSERT IGNORE INTO Subject_Coordinators (user_id)
VALUES ('C001');

INSERT IGNORE INTO subjects (subject_id, subject_name)
VALUES
    ('01', 'Mathematics'),
    ('02', 'Computer Science');

INSERT IGNORE INTO courses
    (course_id, subject_id, coordinator_id, course_name)
VALUES
    ('MATH01', '01', 'C001', 'Plane Geometry'),
    ('CS01', '02', 'C001', 'Introduction to Programming');

INSERT IGNORE INTO teacher_courses (teacher_id, course_id)
VALUES
    ('T001', 'MATH01'),
    ('T001', 'CS01'),
    ('T002', 'CS01');

INSERT IGNORE INTO enrollments (student_id, course_id, grade)
VALUES
    ('S001', 'MATH01', NULL),
    ('S001', 'CS01', NULL),
    ('S002', 'CS01', NULL);

INSERT IGNORE INTO questions
    (question_id, course_id, description, answer1, answer2, answer3, answer4,
     correct_answer, illustrationPath)
VALUES
    ('01001', 'MATH01', 'What is the sum of the angles in a triangle?',
     '90 degrees', '180 degrees', '270 degrees', '360 degrees', 2, NULL),
    ('01002', 'MATH01', 'How many sides does a quadrilateral have?',
     '3', '4', '5', '6', 2, NULL),
    ('01003', 'MATH01', 'What is the area of a rectangle with sides 5 and 3?',
     '8', '15', '16', '30', 2, NULL),
    ('01004', 'MATH01', 'Which angle is greater than 90 degrees but less than 180 degrees?',
     'Acute', 'Right', 'Obtuse', 'Straight', 3, NULL),
    ('02001', 'CS01', 'Which keyword declares a class in Java?',
     'function', 'class', 'object', 'type', 2, NULL),
    ('02002', 'CS01', 'Which Java method is the entry point of an application?',
     'start', 'run', 'main', 'init', 3, NULL),
    ('02003', 'CS01', 'Which type stores a true or false value in Java?',
     'int', 'String', 'boolean', 'double', 3, NULL),
    ('02004', 'CS01', 'Which collection does not allow duplicate elements?',
     'List', 'Set', 'ArrayList', 'Queue', 2, NULL);



SHOW TABLES;

SELECT * FROM Authorized_Users;
SELECT * FROM courses;
SELECT * FROM teacher_courses;
SELECT * FROM enrollments;
SELECT * FROM questions;

SELECT * FROM exams;
SELECT * FROM exam_questions;
SELECT * FROM exam_instances;
SELECT * FROM exam_submissions;
SELECT * FROM submission_answers;
