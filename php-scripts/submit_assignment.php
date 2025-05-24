<?php
require_once("../includes/connection.php");

// extracting data from the post request
$assignment_id = $_POST['assignment_id']; // id of the assignment being submitted
$student_id = $_POST['student_id']; // who is submitting it
$file_name = $_POST['file_name']; // 	original file name
$file_data = base64_decode($_POST['file_data']); // base64-encoded file content (decoded into binary)

// sets the directory where the uploaded files will be stored. 
$upload_dir = "../uploads/";
if (!is_dir($upload_dir)) { // creates it if it doesn’t exist (0777 gives full read/write permissions)
    mkdir($upload_dir, 0777, true);
}
// generating a unique filename to avoid conflicts (like 683f2a6a_homework.pdf). 
$target_file = $upload_dir . uniqid() . "_" . basename($file_name);
file_put_contents($target_file, $file_data); // saves the decoded binary file into that path

// builds the relative file path to store in the DB (for access in the app). Gets today’s date for the submission_date field
$relative_url = "uploads/" . basename($target_file);
$date = date("Y-m-d");

$sql = "INSERT INTO AssignmentSubmission (id, student_id, submission_file_url, submission_date)
        VALUES (?, ?, ?, ?)";

$stmt = $pdo->prepare($sql);
$stmt->execute([$assignment_id, $student_id, $relative_url, $date]);

echo json_encode(["status" => "success"]);
?>
