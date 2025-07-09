<?php
require_once 'connection.php';  // Include the database connection

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if subject_id is provided in the JSON input
if (!isset($input['subject_id'])) {
    echo json_encode(['error' => 'Subject ID is required']);
    exit();
}

$subject_id = $input['subject_id'];

/**
 * Fetch all teachers for a specific subject.
 *
 * @param int $subject_id The subject ID to fetch teachers for.
 * @return array List of teachers for the subject.
 */
function getTeachersForSubject($subject_id) {
    global $pdo;

    // Query to fetch teachers for a specific subject
    $query = "SELECT t.id, t.fname, t.lname, t.email FROM Teacher t
              JOIN Subject s ON t.subject_id = s.id
              WHERE s.id = :subject_id";

    $stmt = $pdo->prepare($query);
    $stmt->bindParam(':subject_id', $subject_id);
    $stmt->execute();

    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Fetch the teachers for the subject
$teachers = getTeachersForSubject($subject_id);

// Return the result as JSON
echo json_encode(['status' => 'success', 'teachers' => $teachers]);
?>
