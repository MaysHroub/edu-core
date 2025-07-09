-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 09, 2025 at 05:24 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `educore_management_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `absence`
--

CREATE TABLE `absence` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `status` enum('excused','unexcused') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `absence`
--

INSERT INTO `absence` (`id`, `student_id`, `date`, `status`) VALUES
(1, 20250016, '2025-06-14', 'unexcused'),
(2, 20250011, '2025-06-14', 'excused');

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

CREATE TABLE `assignment` (
  `id` int(11) NOT NULL,
  `question_file_url` varchar(255) NOT NULL,
  `deadline` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignment`
--

INSERT INTO `assignment` (`id`, `question_file_url`, `deadline`) VALUES
(1, 'uploads/assignments/1/1749888605_684d2e5d2ca41.jpg', '2025-06-23'),
(3, 'uploads/assignments/3/1749890473_684d35a92d165.jpg', '2025-06-20');

-- --------------------------------------------------------

--
-- Table structure for table `assignmentsubmission`
--

CREATE TABLE `assignmentsubmission` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `submission_date` date DEFAULT curdate(),
  `submission_file_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignmentsubmission`
--

INSERT INTO `assignmentsubmission` (`id`, `student_id`, `submission_date`, `submission_file_url`) VALUES
(1, 20250001, '2025-06-14', 'uploads/684d2f40b3156_1200px-Cat03.jpg'),
(3, 20250001, '2025-06-14', 'uploads/684d35f178731_1200px-Cat03.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `authentication`
--

CREATE TABLE `authentication` (
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `user_type` enum('student','teacher','registrar') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `authentication`
--

INSERT INTO `authentication` (`email`, `password`, `user_type`) VALUES
('125001@teacher.educore.edu', 'teacherpass1', 'teacher'),
('125002@teacher.educore.edu', 'teacherpass2', 'teacher'),
('125003@teacher.educore.edu', 'teacherpass3', 'teacher'),
('125004@teacher.educore.edu', 'teacherpass4', 'teacher'),
('125005@teacher.educore.edu', 'teacherpass5', 'teacher'),
('125006@teacher.educore.edu', 'teacherpass6', 'teacher'),
('125007@teacher.educore.edu', 'teacherpass7', 'teacher'),
('125008@teacher.educore.edu', 'teacherpass8', 'teacher'),
('125009@teacher.educore.edu', 'teacherpass9', 'teacher'),
('125010@teacher.educore.edu', 'teacherpass10', 'teacher'),
('125011@teacher.educore.edu', '90#H7*!Z', 'teacher'),
('20250001@student.educore.edu', 'studentpass1', 'student'),
('20250002@student.educore.edu', 'studentpass2', 'student'),
('20250003@student.educore.edu', 'studentpass3', 'student'),
('20250004@student.educore.edu', 'studentpass4', 'student'),
('20250005@student.educore.edu', 'studentpass5', 'student'),
('20250006@student.educore.edu', 'studentpass6', 'student'),
('20250007@student.educore.edu', 'studentpass7', 'student'),
('20250008@student.educore.edu', 'studentpass8', 'student'),
('20250009@student.educore.edu', 'studentpass9', 'student'),
('20250010@student.educore.edu', 'studentpass10', 'student'),
('20250011@student.educore.edu', 'studentpass11', 'student'),
('20250012@student.educore.edu', 'studentpass12', 'student'),
('20250013@student.educore.edu', 'studentpass13', 'student'),
('20250014@student.educore.edu', 'studentpass14', 'student'),
('20250015@student.educore.edu', 'studentpass15', 'student'),
('20250016@student.educore.edu', 'studentpass16', 'student'),
('20250017@student.educore.edu', 'studentpass17', 'student'),
('20250018@student.educore.edu', 'studentpass18', 'student'),
('20250019@student.educore.edu', 'studentpass19', 'student'),
('20250020@student.educore.edu', 'studentpass20', 'student'),
('20250021@student.educore.edu', 'studentpass21', 'student'),
('20250022@student.educore.edu', 'studentpass22', 'student'),
('20250023@student.educore.edu', 'studentpass23', 'student'),
('20250024@student.educore.edu', 'studentpass24', 'student'),
('20250025@student.educore.edu', 'studentpass25', 'student'),
('20250026@student.educore.edu', 'studentpass26', 'student'),
('20250027@student.educore.edu', 'studentpass27', 'student'),
('20250028@student.educore.edu', 'studentpass28', 'student'),
('20250029@student.educore.edu', 'studentpass29', 'student'),
('20250030@student.educore.edu', 'studentpass30', 'student'),
('20250031@student.educore.edu', 'studentpass31', 'student'),
('20250032@student.educore.edu', 'studentpass32', 'student'),
('20250033@student.educore.edu', 'studentpass33', 'student'),
('20250034@student.educore.edu', 'studentpass34', 'student'),
('20250035@student.educore.edu', 'studentpass35', 'student'),
('20250036@student.educore.edu', 'studentpass36', 'student'),
('20250038@student.educore.edu', 'studentpass38', 'student'),
('20250039@student.educore.edu', 'studentpass39', 'student'),
('20250040@student.educore.edu', 'studentpass40', 'student'),
('4001@registrar.educore.edu', 'regpass1', 'registrar'),
('4002@registrar.educore.edu', 'regpass2', 'registrar');

-- --------------------------------------------------------

--
-- Table structure for table `classgrade`
--

CREATE TABLE `classgrade` (
  `grade_number` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classgrade`
--

INSERT INTO `classgrade` (`grade_number`) VALUES
(1),
(2),
(3),
(4),
(5);

-- --------------------------------------------------------

--
-- Table structure for table `classroom`
--

CREATE TABLE `classroom` (
  `id` int(11) NOT NULL,
  `grade_number` int(11) NOT NULL,
  `section` char(1) NOT NULL,
  `homeroom_teacher_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classroom`
--

INSERT INTO `classroom` (`id`, `grade_number`, `section`, `homeroom_teacher_id`) VALUES
(1, 1, 'A', 125001),
(2, 2, 'B', 125002),
(3, 3, 'A', 125003),
(4, 4, 'C', 125004),
(5, 5, 'B', 125005),
(6, 1, 'B', NULL),
(7, 2, 'A', NULL),
(8, 3, 'B', 125008),
(9, 4, 'D', 125009),
(10, 5, 'A', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `registrar`
--

CREATE TABLE `registrar` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `registrar`
--

INSERT INTO `registrar` (`id`, `name`, `email`) VALUES
(4001, 'Anna Williams', '4001@registrar.educore.edu'),
(4002, 'Brian Clark', '4002@registrar.educore.edu');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `fname` varchar(100) NOT NULL,
  `lname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `class_id` int(11) NOT NULL,
  `date_of_birth` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`id`, `fname`, `lname`, `email`, `class_id`, `date_of_birth`) VALUES
(20250001, 'Ahmad', 'Hassan', '20250001@student.educore.edu', 1, '2012-01-10'),
(20250002, 'Omar', 'Ali', '20250002@student.educore.edu', 2, '2012-02-15'),
(20250003, 'Youssef', 'Mahmoud', '20250003@student.educore.edu', 3, '2012-03-20'),
(20250004, 'Mohamed', 'Saeed', '20250004@student.educore.edu', 4, '2012-04-25'),
(20250005, 'Khaled', 'Mostafa', '20250005@student.educore.edu', 5, '2012-05-30'),
(20250006, 'Tarek', 'Fahmy', '20250006@student.educore.edu', 1, '2012-06-05'),
(20250007, 'Hassan', 'Younis', '20250007@student.educore.edu', 2, '2012-07-10'),
(20250008, 'Walid', 'Samir', '20250008@student.educore.edu', 3, '2012-08-15'),
(20250009, 'Ayman', 'Nabil', '20250009@student.educore.edu', 4, '2012-09-20'),
(20250010, 'Moustafa', 'Adel', '20250010@student.educore.edu', 5, '2012-10-25'),
(20250011, 'Hossam', 'Kamel', '20250011@student.educore.edu', 1, '2012-11-30'),
(20250012, 'Karim', 'Salah', '20250012@student.educore.edu', 2, '2012-12-05'),
(20250013, 'Sherif', 'Lotfy', '20250013@student.educore.edu', 3, '2013-01-10'),
(20250014, 'Rami', 'Gamal', '20250014@student.educore.edu', 4, '2013-02-15'),
(20250015, 'Bassel', 'Amin', '20250015@student.educore.edu', 5, '2013-03-20'),
(20250016, 'Fady', 'Hany', '20250016@student.educore.edu', 1, '2013-04-25'),
(20250017, 'Nader', 'Ibrahim', '20250017@student.educore.edu', 2, '2013-05-30'),
(20250018, 'Samy', 'Reda', '20250018@student.educore.edu', 3, '2013-06-05'),
(20250019, 'Yassin', 'Maged', '20250019@student.educore.edu', 4, '2013-07-10'),
(20250020, 'Ziad', 'Ashraf', '20250020@student.educore.edu', 5, '2013-08-15'),
(20250021, 'Ali', 'Farouk', '20250021@student.educore.edu', 6, '2013-09-10'),
(20250022, 'Hatem', 'Sami', '20250022@student.educore.edu', 6, '2013-10-15'),
(20250023, 'Mahmoud', 'Zaki', '20250023@student.educore.edu', 6, '2013-11-20'),
(20250024, 'Mostafa', 'Fathy', '20250024@student.educore.edu', 6, '2013-12-25'),
(20250025, 'Yasser', 'Galal', '20250025@student.educore.edu', 6, '2014-01-30'),
(20250026, 'Kareem', 'Nour', '20250026@student.educore.edu', 7, '2014-02-05'),
(20250027, 'Hossam', 'Aly', '20250027@student.educore.edu', 7, '2014-03-10'),
(20250028, 'Tamer', 'Saber', '20250028@student.educore.edu', 7, '2014-04-15'),
(20250029, 'Ramy', 'Khalil', '20250029@student.educore.edu', 7, '2014-05-20'),
(20250030, 'Yehia', 'Said', '20250030@student.educore.edu', 7, '2014-06-25'),
(20250031, 'Sherif', 'Mounir', '20250031@student.educore.edu', 8, '2014-07-30'),
(20250032, 'Fares', 'Hany', '20250032@student.educore.edu', 8, '2014-08-05'),
(20250033, 'Omar', 'Gad', '20250033@student.educore.edu', 8, '2014-09-10'),
(20250034, 'Yassin', 'Fouad', '20250034@student.educore.edu', 8, '2014-10-15'),
(20250035, 'Ziad', 'Ragab', '20250035@student.educore.edu', 8, '2014-11-20'),
(20250036, 'Bassel', 'Ayman', '20250036@student.educore.edu', 9, '2014-12-25'),
(20250038, 'Fady', 'Ibrahim', '20250038@student.educore.edu', 9, '2015-02-05'),
(20250039, 'Walid', 'Reda', '20250039@student.educore.edu', 9, '2015-03-10'),
(20250040, 'Youssef', 'Lotfy', '20250040@student.educore.edu', 10, '2015-04-15');

-- --------------------------------------------------------

--
-- Table structure for table `subject`
--

CREATE TABLE `subject` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `grade_number` int(11) NOT NULL,
  `semester_number` tinyint(4) NOT NULL CHECK (`semester_number` in (1,2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subject`
--

INSERT INTO `subject` (`id`, `title`, `description`, `grade_number`, `semester_number`) VALUES
(1, 'Mathematics', 'Basic math concepts', 1, 1),
(2, 'Science', 'Introduction to science', 2, 1),
(3, 'English', 'English language basics', 3, 2),
(4, 'History', 'World history overview', 4, 2),
(5, 'Art', 'Fundamentals of art', 5, 1),
(6, 'Physics', 'Basic introduction', 5, 1);

-- --------------------------------------------------------

--
-- Table structure for table `task`
--

CREATE TABLE `task` (
  `id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `type` enum('Assignment','Quiz','First','Second','Midterm','Final') NOT NULL,
  `max_score` decimal(5,2) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `date` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `task`
--

INSERT INTO `task` (`id`, `subject_id`, `class_id`, `teacher_id`, `type`, `max_score`, `title`, `description`, `date`) VALUES
(1, 1, 1, 125001, 'Assignment', 20.00, 'assign1', 'basic algebra', '2025-06-14'),
(2, 1, 1, 125001, 'Quiz', 10.00, 'quiz1', 'Exam Type: Quiz\n\nquiz in chapter 1', '2025-06-30'),
(3, 1, 1, 125001, 'Assignment', 20.00, 'assign1', 'basic algebra', '2025-06-14');

-- --------------------------------------------------------

--
-- Table structure for table `taskresult`
--

CREATE TABLE `taskresult` (
  `task_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `mark` decimal(5,2) DEFAULT NULL,
  `feedback` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `taskresult`
--

INSERT INTO `taskresult` (`task_id`, `student_id`, `mark`, `feedback`) VALUES
(1, 20250001, 10.00, 'Good!'),
(3, 20250001, 18.00, 'Good');

-- --------------------------------------------------------

--
-- Table structure for table `teacher`
--

CREATE TABLE `teacher` (
  `id` int(11) NOT NULL,
  `fname` varchar(100) NOT NULL,
  `lname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `subject_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher`
--

INSERT INTO `teacher` (`id`, `fname`, `lname`, `email`, `phone_number`, `date_of_birth`, `subject_id`) VALUES
(125001, 'Emily', 'Johnson', '125001@teacher.educore.edu', '555-2001', '1985-02-10', 1),
(125002, 'Sophia', 'Williams', '125002@teacher.educore.edu', '555-2002', '1986-03-15', 2),
(125003, 'Olivia', 'Brown', '125003@teacher.educore.edu', '555-2003', '1987-04-20', 3),
(125004, 'Ava', 'Jones', '125004@teacher.educore.edu', '555-2004', '1988-05-25', 4),
(125005, 'Isabella', 'Garcia', '125005@teacher.educore.edu', '555-2005', '1989-06-30', 5),
(125006, 'Mia', 'Martinez', '125006@teacher.educore.edu', '555-2006', '1990-07-05', 1),
(125007, 'Charlotte', 'Davis', '125007@teacher.educore.edu', '555-2007', '1991-08-10', 2),
(125008, 'Amelia', 'Lopez', '125008@teacher.educore.edu', '555-2008', '1992-09-15', 3),
(125009, 'Harper', 'Gonzalez', '125009@teacher.educore.edu', '555-2009', '1993-10-20', 4),
(125010, 'Evelyn', 'Wilson', '125010@teacher.educore.edu', '555-2010', '1994-11-25', 5),
(125011, 'Mays Al-Reem', 'Hroub', '125011@teacher.educore.edu', '123456789', '2007-06-14', 6);

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

CREATE TABLE `timetable` (
  `teacher_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `day_of_week` enum('Sunday','Monday','Tuesday','Wednesday','Thursday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `timetable`
--

INSERT INTO `timetable` (`teacher_id`, `subject_id`, `class_id`, `day_of_week`, `start_time`, `end_time`) VALUES
(125001, 1, 1, 'Sunday', '08:00:00', '08:50:00'),
(125001, 1, 1, 'Sunday', '09:00:00', '09:50:00'),
(125001, 1, 1, 'Tuesday', '09:00:00', '09:50:00'),
(125001, 1, 1, 'Thursday', '09:00:00', '09:50:00'),
(125002, 2, 2, 'Sunday', '10:00:00', '10:50:00'),
(125002, 2, 2, 'Monday', '10:00:00', '10:50:00'),
(125002, 2, 2, 'Thursday', '10:00:00', '10:50:00'),
(125006, 1, 1, 'Monday', '11:30:00', '12:20:00'),
(125006, 1, 1, 'Wednesday', '12:30:00', '13:20:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `absence`
--
ALTER TABLE `absence`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `assignment`
--
ALTER TABLE `assignment`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `assignmentsubmission`
--
ALTER TABLE `assignmentsubmission`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `authentication`
--
ALTER TABLE `authentication`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `classgrade`
--
ALTER TABLE `classgrade`
  ADD PRIMARY KEY (`grade_number`);

--
-- Indexes for table `classroom`
--
ALTER TABLE `classroom`
  ADD PRIMARY KEY (`id`),
  ADD KEY `grade_number` (`grade_number`),
  ADD KEY `homeroom_teacher_id` (`homeroom_teacher_id`);

--
-- Indexes for table `registrar`
--
ALTER TABLE `registrar`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `class_id` (`class_id`);

--
-- Indexes for table `subject`
--
ALTER TABLE `subject`
  ADD PRIMARY KEY (`id`),
  ADD KEY `grade_number` (`grade_number`);

--
-- Indexes for table `task`
--
ALTER TABLE `task`
  ADD PRIMARY KEY (`id`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `taskresult`
--
ALTER TABLE `taskresult`
  ADD PRIMARY KEY (`task_id`,`student_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `teacher`
--
ALTER TABLE `teacher`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `timetable`
--
ALTER TABLE `timetable`
  ADD PRIMARY KEY (`teacher_id`,`class_id`,`day_of_week`,`start_time`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `class_id` (`class_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `absence`
--
ALTER TABLE `absence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `classroom`
--
ALTER TABLE `classroom`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `registrar`
--
ALTER TABLE `registrar`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4003;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20250041;

--
-- AUTO_INCREMENT for table `subject`
--
ALTER TABLE `subject`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `task`
--
ALTER TABLE `task`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `teacher`
--
ALTER TABLE `teacher`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=125012;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `absence`
--
ALTER TABLE `absence`
  ADD CONSTRAINT `absence_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `assignment`
--
ALTER TABLE `assignment`
  ADD CONSTRAINT `assignment_ibfk_1` FOREIGN KEY (`id`) REFERENCES `task` (`id`);

--
-- Constraints for table `assignmentsubmission`
--
ALTER TABLE `assignmentsubmission`
  ADD CONSTRAINT `assignmentsubmission_ibfk_1` FOREIGN KEY (`id`) REFERENCES `assignment` (`id`),
  ADD CONSTRAINT `assignmentsubmission_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `classroom`
--
ALTER TABLE `classroom`
  ADD CONSTRAINT `classroom_ibfk_1` FOREIGN KEY (`grade_number`) REFERENCES `classgrade` (`grade_number`),
  ADD CONSTRAINT `classroom_ibfk_2` FOREIGN KEY (`homeroom_teacher_id`) REFERENCES `teacher` (`id`);

--
-- Constraints for table `registrar`
--
ALTER TABLE `registrar`
  ADD CONSTRAINT `registrar_ibfk_1` FOREIGN KEY (`email`) REFERENCES `authentication` (`email`);

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`email`) REFERENCES `authentication` (`email`),
  ADD CONSTRAINT `student_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classroom` (`id`);

--
-- Constraints for table `subject`
--
ALTER TABLE `subject`
  ADD CONSTRAINT `subject_ibfk_1` FOREIGN KEY (`grade_number`) REFERENCES `classgrade` (`grade_number`);

--
-- Constraints for table `task`
--
ALTER TABLE `task`
  ADD CONSTRAINT `task_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  ADD CONSTRAINT `task_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classroom` (`id`),
  ADD CONSTRAINT `task_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`);

--
-- Constraints for table `taskresult`
--
ALTER TABLE `taskresult`
  ADD CONSTRAINT `taskresult_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
  ADD CONSTRAINT `taskresult_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `teacher`
--
ALTER TABLE `teacher`
  ADD CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`email`) REFERENCES `authentication` (`email`),
  ADD CONSTRAINT `teacher_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`);

--
-- Constraints for table `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`),
  ADD CONSTRAINT `timetable_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  ADD CONSTRAINT `timetable_ibfk_3` FOREIGN KEY (`class_id`) REFERENCES `classroom` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
