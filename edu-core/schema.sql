DROP DATABASE IF EXISTS educore_management_system;
CREATE DATABASE educore_management_system;
USE educore_management_system;


CREATE TABLE Authentication (
    email VARCHAR(100) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    user_type ENUM('student', 'teacher', 'registrar') NOT NULL
);

CREATE TABLE ClassGrade (
    grade_number INT PRIMARY KEY
);

CREATE TABLE Subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    grade_number INT NOT NULL,
    semester_number TINYINT NOT NULL CHECK (semester_number IN (1, 2)),
    FOREIGN KEY (grade_number) REFERENCES ClassGrade(grade_number)
    -- add teacher_id since a subject can be taught by multiple teachers
);

CREATE TABLE Teacher (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fname VARCHAR(100) NOT NULL,
    lname VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    date_of_birth DATE,
    subject_id INT, -- remove it
    FOREIGN KEY (email) REFERENCES Authentication(email),
    FOREIGN KEY (subject_id) REFERENCES Subject(id)
);
ALTER TABLE Teacher AUTO_INCREMENT = 125001;

CREATE TABLE Classroom (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grade_number INT NOT NULL,
    section CHAR(1) NOT NULL,
    homeroom_teacher_id INT,
    FOREIGN KEY (grade_number) REFERENCES ClassGrade(grade_number),
    FOREIGN KEY (homeroom_teacher_id) REFERENCES Teacher(id)
);

CREATE TABLE Student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fname VARCHAR(100) NOT NULL,
    lname VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    class_id INT NOT NULL,
    date_of_birth DATE,
    FOREIGN KEY (email) REFERENCES Authentication(email),
    FOREIGN KEY (class_id) REFERENCES Classroom(id)
);
ALTER TABLE Student AUTO_INCREMENT = 20250001;

CREATE TABLE Registrar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    FOREIGN KEY (email) REFERENCES Authentication(email)
);
ALTER TABLE Registrar AUTO_INCREMENT = 4001;

CREATE TABLE TimeTable (
    teacher_id INT NOT NULL,
    subject_id INT NOT NULL,
    class_id INT NOT NULL,
    day_of_week ENUM ('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    PRIMARY KEY (teacher_id, class_id, day_of_week, start_time),
    FOREIGN KEY (teacher_id) REFERENCES Teacher(id),
    FOREIGN KEY (subject_id) REFERENCES Subject(id),
    FOREIGN KEY (class_id) REFERENCES Classroom(id)
);

CREATE TABLE Task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    class_id INT NOT NULL,
    teacher_id INT NOT NULL,
    type ENUM('Assignment','Quiz','First','Second','Midterm','Final') NOT NULL,
    max_score DECIMAL(5, 2) NOT NULL,
    title VARCHAR(100) NOT NULL,  -- added title column
    description TEXT,  -- added description column
    date DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (subject_id) REFERENCES Subject(id),
    FOREIGN KEY (class_id) REFERENCES Classroom(id),
    FOREIGN KEY (teacher_id) REFERENCES Teacher(id)
);

CREATE TABLE Assignment (
    id INT PRIMARY KEY,
    question_file_url VARCHAR(255) NOT NULL,
    deadline DATE,
    FOREIGN KEY (id) REFERENCES Task(id)
);

CREATE TABLE AssignmentSubmission (
    id INT PRIMARY KEY,
    student_id INT NOT NULL,
    submission_date DATE DEFAULT CURRENT_DATE,
    submission_file_url VARCHAR(255),
    FOREIGN KEY (id) REFERENCES Assignment(id),
    FOREIGN KEY (student_id) REFERENCES Student(id)
);

CREATE TABLE TaskResult (
    task_id INT,
    student_id INT,
    mark DECIMAL(5, 2),
    feedback VARCHAR(255), -- added feedback column
    PRIMARY KEY (task_id, student_id),
    FOREIGN KEY (task_id) REFERENCES Task(id),
    FOREIGN KEY (student_id) REFERENCES Student(id)
);

CREATE TABLE Absence (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('excused', 'unexcused') NOT NULL, -- added status column
    FOREIGN KEY (student_id) REFERENCES Student(id)
);