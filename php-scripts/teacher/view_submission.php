<?php
// view_submission.php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'connection.php';

try {
    // Get parameters from URL
    $taskId = isset($_GET['task_id']) ? intval($_GET['task_id']) : -1;
    $studentId = isset($_GET['student_id']) ? $_GET['student_id'] : '';
    
    // Validate required parameters
    if ($taskId < 0 || empty($studentId)) {
        http_response_code(400);
        echo json_encode(["error" => "Missing required parameters: task_id and student_id"]);
        exit();
    }

    // Verify that the task exists and is an assignment
    $sqlTask = "SELECT t.id, t.type FROM Task t WHERE t.id = ? AND t.type = 'Assignment'";
    $stmtTask = $pdo->prepare($sqlTask);
    $stmtTask->execute([$taskId]);
    $resultTask = $stmtTask->fetch(PDO::FETCH_ASSOC);
    
    if (!$resultTask) {
        http_response_code(404);
        echo json_encode(["error" => "Assignment not found"]);
        exit();
    }

    // Get submission file URL from database
    $sqlSubmission = "SELECT submission_file_url, submission_date 
                      FROM AssignmentSubmission 
                      WHERE id = ? AND student_id = ?";
    $stmtSubmission = $pdo->prepare($sqlSubmission);
    $stmtSubmission->execute([$taskId, $studentId]);
    $submissionRow = $stmtSubmission->fetch(PDO::FETCH_ASSOC);
    
    if (!$submissionRow) {
        http_response_code(404);
        echo json_encode(["error" => "No submission found for this student"]);
        exit();
    }
    
    $fileUrl = $submissionRow['submission_file_url'];
    $submissionDate = $submissionRow['submission_date'];
    
    // Check if file URL is empty
    if (empty($fileUrl)) {
        http_response_code(404);
        echo json_encode(["error" => "No file submitted"]);
        exit();
    }
    
    // Construct full file path
    $filePath = $fileUrl; // This should be something like "uploads/assignments/123/filename.pdf"
    
    // Security check: ensure the file path is within the uploads directory
    $realPath = realpath($filePath);
    $uploadsPath = realpath('uploads/assignments/');
    
    if (!$realPath || !$uploadsPath || strpos($realPath, $uploadsPath) !== 0) {
        http_response_code(403);
        echo json_encode(["error" => "Access denied"]);
        exit();
    }
    
    // Check if file exists
    if (!file_exists($filePath)) {
        http_response_code(404);
        echo json_encode(["error" => "Submission file not found on server"]);
        exit();
    }
    
    // Get file information
    $fileName = basename($filePath);
    $fileSize = filesize($filePath);
    $mimeType = mime_content_type($filePath);
    
    // If it's a download request, serve the file
    if (isset($_GET['download']) && $_GET['download'] == '1') {
        // Set headers for file download
        header('Content-Type: ' . $mimeType);
        header('Content-Disposition: attachment; filename="' . $fileName . '"');
        header('Content-Length: ' . $fileSize);
        header('Cache-Control: no-cache, must-revalidate');
        header('Expires: Sat, 26 Jul 1997 05:00:00 GMT');
        
        // Output file content
        readfile($filePath);
        exit();
    }
    
    // If it's a view request (for images/PDFs), serve inline
    if (isset($_GET['view']) && $_GET['view'] == '1') {
        // Set headers for inline viewing
        header('Content-Type: ' . $mimeType);
        header('Content-Disposition: inline; filename="' . $fileName . '"');
        header('Content-Length: ' . $fileSize);
        header('Cache-Control: public, max-age=3600');
        
        // Output file content
        readfile($filePath);
        exit();
    }
    
    // Default: return file information as JSON
    echo json_encode([
        "success" => true,
        "file_name" => $fileName,
        "file_size" => $fileSize,
        "file_type" => $mimeType,
        "submission_date" => $submissionDate,
        "download_url" => "view_submission.php?task_id=" . $taskId . "&student_id=" . urlencode($studentId) . "&download=1",
        "view_url" => "view_submission.php?task_id=" . $taskId . "&student_id=" . urlencode($studentId) . "&view=1"
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Server error: " . $e->getMessage()]);
}
?>
