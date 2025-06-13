<?php
header('Content-Type: application/json');
require_once 'connection.php';  // Include the database connection

// Get the request data
$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$current_password = $data['current_password'] ?? '';
$new_password = $data['new_password'] ?? '';
$confirm_password = $data['confirm_password'] ?? '';

if (empty($email) || empty($current_password) || empty($new_password) || empty($confirm_password)) {
    echo json_encode(['success' => false, 'message' => 'All fields are required']);
    exit;
}

if ($new_password !== $confirm_password) {
    echo json_encode(['success' => false, 'message' => 'New password and confirm password do not match']);
    exit;
}

try {
    // Fetch the stored password from the database
    $stmt = $pdo->prepare("SELECT password FROM Teacher WHERE email = ?");
    $stmt->execute([$email]);
    $teacher = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($teacher) {
        // Check if the provided current password matches the stored one
        if (password_verify($current_password, $teacher['password'])) {
            // Hash the new password
            $hashed_password = password_hash($new_password, PASSWORD_DEFAULT);

            // Update the teacher's password in the database
            $update_stmt = $pdo->prepare("UPDATE Teacher SET password = ? WHERE email = ?");
            $update_result = $update_stmt->execute([$hashed_password, $email]);

            if ($update_result) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Password changed successfully'
                ]);
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Failed to change password'
                ]);
            }
        } else {
            echo json_encode([
                'success' => false,
                'message' => 'Current password is incorrect'
            ]);
        }
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Teacher not found'
        ]);
    }
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>
