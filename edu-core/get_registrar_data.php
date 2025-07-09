<?php
// Include the database connection file
header('Content-Type: application/json');
require_once 'connection.php';  // Assuming you have your connection file here

// Get the input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Check if email is provided in the JSON input
if (!isset($input['email'])) {
    echo json_encode(['error' => 'Email is required']);
    exit();
}

$email = $input['email'];

/**
 * Fetch registrar data by email.
 *
 * @param string $email The email of the registrar.
 * @return array The registrar data or null if not found.
 */
function getRegistrarDataByEmail($email) {
    global $pdo;

    $sql = "SELECT id, name, email
            FROM Registrar
            WHERE email = :email";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['email' => $email]);

    $registrar = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($registrar) {
        return $registrar;
    } else {
        return null; // Registrar not found
    }
}

// Fetch registrar data
$registrar = getRegistrarDataByEmail($email);

if ($registrar) {
    echo json_encode(['status' => 'success', 'registrar' => $registrar]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Registrar not found']);
}
?>
