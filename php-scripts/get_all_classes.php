<?php
require_once 'connection.php';  // Include the database connection

// Get the input JSON (optional, not needed for this method but you could add filtering if needed in the future)
$input = json_decode(file_get_contents('php://input'), true);

/**
 * Fetch all classes from the database.
 *
 * @return array List of all classes.
 */
function getAllClasses() {
    global $pdo;

    // Query to fetch all classes
    $query = "SELECT c.id, c.grade_number, c.section, t.fname AS homeroom_teacher_fname, t.lname AS homeroom_teacher_lname 
              FROM Classroom c
              LEFT JOIN Teacher t ON c.homeroom_teacher_id = t.id";

    $stmt = $pdo->prepare($query);
    $stmt->execute();

    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Fetch all classes
$classes = getAllClasses();

// Return the result as JSON
echo json_encode(['status' => 'success', 'classes' => $classes]);
?>
