-- Insert records into Authentication for teachers
INSERT INTO Authentication (email, password, user_type) 
VALUES
('125001@teacher.educore.edu', 'teacherpass1', 'teacher'),
('125002@teacher.educore.edu', 'teacherpass2', 'teacher'),
('125003@teacher.educore.edu', 'teacherpass3', 'teacher'),
('125004@teacher.educore.edu', 'teacherpass4', 'teacher'),
('125005@teacher.educore.edu', 'teacherpass5', 'teacher'),
('125006@teacher.educore.edu', 'teacherpass6', 'teacher'),
('125007@teacher.educore.edu', 'teacherpass7', 'teacher'),
('125008@teacher.educore.edu', 'teacherpass8', 'teacher'),
('125009@teacher.educore.edu', 'teacherpass9', 'teacher'),
('125010@teacher.educore.edu', 'teacherpass10', 'teacher');

-- Insert records into Authentication for students
INSERT INTO Authentication (email, password, user_type) 
VALUES
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
('20250037@student.educore.edu', 'studentpass37', 'student'),
('20250038@student.educore.edu', 'studentpass38', 'student'),
('20250039@student.educore.edu', 'studentpass39', 'student'),
('20250040@student.educore.edu', 'studentpass40', 'student');

-- Insert records into Authentication for registrars (including 2 more)
INSERT INTO Authentication (email, password, user_type) 
VALUES
('4001@registrar.educore.edu', 'regpass1', 'registrar'),
('4002@registrar.educore.edu', 'regpass2', 'registrar');


INSERT INTO Registrar (name, email)
VALUES
('Anna Williams', '4001@registrar.educore.edu'),
('Brian Clark', '4002@registrar.educore.edu');

-- Insert records into ClassGrade
INSERT INTO ClassGrade (grade_number) 
VALUES
(1), (2), (3), (4), (5);

-- Insert records into Subject
INSERT INTO Subject (title, description, grade_number, semester_number) 
VALUES
('Mathematics', 'Basic math concepts', 1, 1),
('Science', 'Introduction to science', 2, 1),
('English', 'English language basics', 3, 2),
('History', 'World history overview', 4, 2),
('Art', 'Fundamentals of art', 5, 1),
('Physical Education', 'Basics of physical fitness', 1, 2),
('Music', 'Basics of music theory', 3, 2),
('Geography', 'World geography overview', 4, 1),
('Biology', 'Introduction to biology', 5, 2),
('Literature', 'Overview of classic literature', 3, 1);

-- Insert records into Teacher
INSERT INTO Teacher (fname, lname, email, phone_number, date_of_birth, subject_id)
VALUES
('Emily', 'Johnson', '125001@teacher.educore.edu', '555-2001', '1985-02-10', 1),
('Sophia', 'Williams', '125002@teacher.educore.edu', '555-2002', '1986-03-15', 2),
('Olivia', 'Brown', '125003@teacher.educore.edu', '555-2003', '1987-04-20', 3),
('Ava', 'Jones', '125004@teacher.educore.edu', '555-2004', '1988-05-25', 4),
('Isabella', 'Garcia', '125005@teacher.educore.edu', '555-2005', '1989-06-30', 5),
('Mia', 'Martinez', '125006@teacher.educore.edu', '555-2006', '1990-07-05', 1),
('Charlotte', 'Davis', '125007@teacher.educore.edu', '555-2007', '1991-08-10', 2),
('Amelia', 'Lopez', '125008@teacher.educore.edu', '555-2008', '1992-09-15', 3),
('Harper', 'Gonzalez', '125009@teacher.educore.edu', '555-2009', '1993-10-20', 4),
('Evelyn', 'Wilson', '125010@teacher.educore.edu', '555-2010', '1994-11-25', 5);

-- Insert records into Classroom
INSERT INTO Classroom (grade_number, section, homeroom_teacher_id)
VALUES
(1, 'A', 125001),
(2, 'B', 125002),
(3, 'A', 125003),
(4, 'C', 125004),
(5, 'B', 125005),
(1, 'B', null),
(2, 'A', null),
(3, 'B', 125008),
(4, 'D', 125009),
(5, 'A', null);

-- Insert records into Student
INSERT INTO Student (fname, lname, email, class_id, date_of_birth)
VALUES
('Ahmed', 'Hassan', '20250001@student.educore.edu', 1, '2012-01-10'),
('Omar', 'Ali', '20250002@student.educore.edu', 2, '2012-02-15'),
('Youssef', 'Mahmoud', '20250003@student.educore.edu', 3, '2012-03-20'),
('Mohamed', 'Saeed', '20250004@student.educore.edu', 4, '2012-04-25'),
('Khaled', 'Mostafa', '20250005@student.educore.edu', 5, '2012-05-30'),
('Tarek', 'Fahmy', '20250006@student.educore.edu', 1, '2012-06-05'),
('Hassan', 'Younis', '20250007@student.educore.edu', 2, '2012-07-10'),
('Walid', 'Samir', '20250008@student.educore.edu', 3, '2012-08-15'),
('Ayman', 'Nabil', '20250009@student.educore.edu', 4, '2012-09-20'),
('Moustafa', 'Adel', '20250010@student.educore.edu', 5, '2012-10-25'),
('Hossam', 'Kamel', '20250011@student.educore.edu', 1, '2012-11-30'),
('Karim', 'Salah', '20250012@student.educore.edu', 2, '2012-12-05'),
('Sherif', 'Lotfy', '20250013@student.educore.edu', 3, '2013-01-10'),
('Rami', 'Gamal', '20250014@student.educore.edu', 4, '2013-02-15'),
('Bassel', 'Amin', '20250015@student.educore.edu', 5, '2013-03-20'),
('Fady', 'Hany', '20250016@student.educore.edu', 1, '2013-04-25'),
('Nader', 'Ibrahim', '20250017@student.educore.edu', 2, '2013-05-30'),
('Samy', 'Reda', '20250018@student.educore.edu', 3, '2013-06-05'),
('Yassin', 'Maged', '20250019@student.educore.edu', 4, '2013-07-10'),
('Ziad', 'Ashraf', '20250020@student.educore.edu', 5, '2013-08-15'),
('Ali', 'Farouk', '20250021@student.educore.edu', 6, '2013-09-10'),
('Hatem', 'Sami', '20250022@student.educore.edu', 6, '2013-10-15'),
('Mahmoud', 'Zaki', '20250023@student.educore.edu', 6, '2013-11-20'),
('Mostafa', 'Fathy', '20250024@student.educore.edu', 6, '2013-12-25'),
('Yasser', 'Galal', '20250025@student.educore.edu', 6, '2014-01-30'),
('Kareem', 'Nour', '20250026@student.educore.edu', 7, '2014-02-05'),
('Hossam', 'Aly', '20250027@student.educore.edu', 7, '2014-03-10'),
('Tamer', 'Saber', '20250028@student.educore.edu', 7, '2014-04-15'),
('Ramy', 'Khalil', '20250029@student.educore.edu', 7, '2014-05-20'),
('Yehia', 'Said', '20250030@student.educore.edu', 7, '2014-06-25'),
('Sherif', 'Mounir', '20250031@student.educore.edu', 8, '2014-07-30'),
('Fares', 'Hany', '20250032@student.educore.edu', 8, '2014-08-05'),
('Omar', 'Gad', '20250033@student.educore.edu', 8, '2014-09-10'),
('Yassin', 'Fouad', '20250034@student.educore.edu', 8, '2014-10-15'),
('Ziad', 'Ragab', '20250035@student.educore.edu', 8, '2014-11-20'),
('Bassel', 'Ayman', '20250036@student.educore.edu', 9, '2014-12-25'),
('Nader', 'Samy', '20250037@student.educore.edu', 9, '2015-01-30'),
('Fady', 'Ibrahim', '20250038@student.educore.edu', 9, '2015-02-05'),
('Walid', 'Reda', '20250039@student.educore.edu', 9, '2015-03-10'),
('Youssef', 'Lotfy', '20250040@student.educore.edu', 10, '2015-04-15');

-- Insert records into Timetable
-- INSERT INTO Timetable (class_id, subject_id, teacher_id, day_of_week, start_time, end_time) 
-- VALUES
-- (1, 1, 125001, 'Monday', '08:00:00', '09:00:00'),
-- (1, 6, 125001, 'Wednesday', '09:00:00', '10:00:00'),
-- (1, 10, 125001, 'Thursday', '10:00:00', '11:00:00'),
-- (2, 2, 125002, 'Monday', '08:00:00', '09:00:00'),
-- (2, 11, 125002, 'Tuesday', '09:00:00', '10:00:00'),
-- (2, 5, 125002, 'Thursday', '10:00:00', '11:00:00'),
-- (3, 3, 125003, 'Monday', '08:00:00', '09:00:00'),
-- (3, 7, 125003, 'Wednesday', '09:00:00', '10:00:00'),
-- (3, 12, 125003, 'Friday', '10:00:00', '11:00:00'),
-- (4, 4, 125004, 'Tuesday', '08:00:00', '09:00:00'),
-- (4, 8, 125004, 'Wednesday', '09:00:00', '10:00:00'),
-- (4, 9, 125004, 'Thursday', '10:00:00', '11:00:00'),
-- (5, 5, 125005, 'Monday', '08:00:00', '09:00:00'),
-- (5, 9, 125005, 'Tuesday', '09:00:00', '10:00:00'),
-- (5, 4, 125005, 'Friday', '10:00:00', '11:00:00'),
-- (6, 1, 125001, 'Monday', '09:00:00', '10:00:00'),
-- (6, 6, 125001, 'Wednesday', '10:00:00', '11:00:00'),
-- (6, 10, 125001, 'Thursday', '11:00:00', '12:00:00'),
-- (7, 2, 125002, 'Tuesday', '09:00:00', '10:00:00'),
-- (7, 11, 125002, 'Wednesday', '10:00:00', '11:00:00'),
-- (7, 5, 125002, 'Thursday', '11:00:00', '12:00:00'),
-- (8, 3, 125003, 'Monday', '10:00:00', '11:00:00'),
-- (8, 7, 125003, 'Tuesday', '11:00:00', '12:00:00'),
-- (8, 12, 125003, 'Friday', '12:00:00', '13:00:00'),
-- (9, 4, 125004, 'Wednesday', '10:00:00', '11:00:00'),
-- (9, 8, 125004, 'Thursday', '11:00:00', '12:00:00'),
-- (9, 9, 125004, 'Friday', '12:00:00', '13:00:00'),
-- (10, 5, 125005, 'Monday', '11:00:00', '12:00:00'),
-- (10, 9, 125005, 'Tuesday', '12:00:00', '13:00:00'),
-- (10, 4, 125005, 'Wednesday', '13:00:00', '14:00:00');

-- Insert records into Task
INSERT INTO Task (subject_id, class_id, teacher_id, type, max_score, title, description, date)
VALUES
(1, 1, 125001, 'Assignment', 10.00, 'Math Homework 1', 'Solve exercises 1-10 from the textbook.', '2025-09-10'),
(2, 2, 125002, 'Quiz', 20.00, 'Science Quiz 1', 'Quiz on Chapter 2: Plants and Animals.', '2025-09-12'),
(3, 3, 125003, 'Midterm', 50.00, 'English Midterm', 'Midterm exam covering units 1-3.', '2025-09-15'),
(4, 1, 125004, 'Assignment', 15.00, 'History Assignment', 'Write a report on World War II.', '2025-09-18'),
(5, 5, 125005, 'Final', 100.00, 'Art Final Project', 'Create a portfolio of your best artwork.', '2025-09-20'),
(1, 1, 125001, 'Assignment', 10.00, 'Math Homework 2', 'Solve exercises 11-20 from the textbook.', '2025-09-22'),
(2, 6, 125001, 'Quiz', 20.00, 'Science Quiz 2', 'Quiz on Chapter 3: The Solar System.', '2025-09-24'),
(3, 6, 125001, 'Midterm', 50.00, 'English Midterm II', 'Midterm exam covering units 4-6.', '2025-09-27'),
(4, 1, 125004, 'Assignment', 15.00, 'History Assignment II', 'Write a report on the Cold War.', '2025-09-30'),
(5, 10, 125005, 'Final', 100.00, 'Art Final Project II', 'Create a new portfolio of your best artwork.', '2025-10-02'),
(2, 2, 125002, 'Assignment', 12.00, 'Science Assignment 1', 'Complete the worksheet on plants.', '2025-09-25'),
(3, 3, 125003, 'Assignment', 15.00, 'English Assignment 1', 'Write a short story.', '2025-09-26'),
(4, 4, 125004, 'Assignment', 10.00, 'History Assignment 1', 'Timeline of World War I.', '2025-09-27'),
(5, 5, 125005, 'Assignment', 20.00, 'Art Assignment 1', 'Draw a landscape.', '2025-09-28'),
(6, 6, 125006, 'Assignment', 10.00, 'PE Assignment 1', 'Log your daily exercises.', '2025-09-29'),
(7, 7, 125007, 'Assignment', 15.00, 'Music Assignment 1', 'Compose a simple melody.', '2025-09-30'),
(8, 8, 125008, 'Assignment', 10.00, 'Geography Assignment 1', 'Map the continents.', '2025-10-01'),
(9, 9, 125009, 'Assignment', 12.00, 'Biology Assignment 1', 'Label the parts of a cell.', '2025-10-02'),
(10, 10, 125010, 'Assignment', 18.00, 'Chemistry Assignment 1', 'Periodic table worksheet.', '2025-10-03'),
(11, 1, 125001, 'Assignment', 14.00, 'Math Assignment 2', 'Solve the algebra problems.', '2025-10-04');


-- Insert records into Assignment
INSERT INTO Assignment (id, question_file_url, deadline)
VALUES
(1, 'https://example.com/questions/math_homework1.pdf', '2025-09-15'),
(4, 'https://example.com/questions/science_quiz1.pdf', '2025-09-13'),
(6, 'https://example.com/questions/english_midterm.pdf', '2025-09-20'),
(9, 'https://example.com/questions/history_assignment.pdf', '2025-09-22'),
(11, 'https://example.com/questions/science_assignment1.pdf', '2025-09-30'),
(12, 'https://example.com/questions/english_assignment1.pdf', '2025-10-01'),
(13, 'https://example.com/questions/history_assignment1.pdf', '2025-10-02'),
(14, 'https://example.com/questions/art_assignment1.pdf', '2025-10-03'),
(15, 'https://example.com/questions/pe_assignment1.pdf', '2025-10-04'),
(16, 'https://example.com/questions/music_assignment1.pdf', '2025-10-05'),
(17, 'https://example.com/questions/geography_assignment1.pdf', '2025-10-06'),
(18, 'https://example.com/questions/biology_assignment1.pdf', '2025-10-07'),
(19, 'https://example.com/questions/chemistry_assignment1.pdf', '2025-10-08'),
(20, 'https://example.com/questions/math_assignment2.pdf', '2025-10-09');


-- Insert records into TaskResult
-- INSERT INTO TaskResult (task_id, student_id, mark, feedback)
-- VALUES
-- (1, 20250001, 9.5, 'Great job on the math homework!'),
-- (2, 20250005, 18.0, 'Well done on the science quiz.'),
-- (3, 20250004, 45.0, 'Good effort on the midterm.'),
-- (4, 20250003, 13.5, 'Nice report on history.'),
-- (5, 20250002, 95.0, 'Excellent art portfolio!');

