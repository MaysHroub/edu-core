<?php
header('Content-Type: application/json');

// Include the database connection file
require_once 'connection.php'; // This will include the $pdo object

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    
    $email = $data['email'] ?? '';
    $password = $data['password'] ?? '';
    
    if (empty($email) || empty($password)) {
        echo json_encode(['success' => false, 'message' => 'Missing required fields']);
        exit;
    }
    
    // Query the Authentication table using PDO
    $stmt = $pdo->prepare("SELECT password, user_type FROM Authentication WHERE email = :email");
    $stmt->bindParam(':email', $email, PDO::PARAM_STR);
    
    // Execute the query
    if (!$stmt->execute()) {
        echo json_encode(['success' => false, 'message' => 'Query execution failed']);
        exit;
    }
    
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result) {
        // Use password_verify() for secure password comparison
        if ($password== $result['password']) {
            echo json_encode([
                'success' => true,
                'message' => 'Login successful',
                'user_type' => $result['user_type']
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Invalid password']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'User not found']);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request method']);
}

// Close PDO connection (optional, it's closed automatically at the end of the script)
$pdo = null;
?>
