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
        if ($password == $result['password']) {
            $response = [
                'success' => true,
                'message' => 'Login successful',
                'user_type' => $result['user_type']
            ];
            
            // Get user ID based on user type
            $userId = null;
            switch ($result['user_type']) {
                case 'teacher':
                    $teacherStmt = $pdo->prepare("SELECT id FROM teacher WHERE email = :email");
                    $teacherStmt->bindParam(':email', $email, PDO::PARAM_STR);
                    if ($teacherStmt->execute()) {
                        $teacherResult = $teacherStmt->fetch(PDO::FETCH_ASSOC);
                        if ($teacherResult) {
                            $userId = $teacherResult['id'];
                        }
                    }
                    break;
                    
                case 'student':
                    $studentStmt = $pdo->prepare("SELECT id FROM student WHERE email = :email");
                    $studentStmt->bindParam(':email', $email, PDO::PARAM_STR);
                    if ($studentStmt->execute()) {
                        $studentResult = $studentStmt->fetch(PDO::FETCH_ASSOC);
                        if ($studentResult) {
                            $userId = $studentResult['id'];
                        }
                    }
                    break;
                    
                case 'registrar':
                    $registrarStmt = $pdo->prepare("SELECT id FROM registrar WHERE email = :email");
                    $registrarStmt->bindParam(':email', $email, PDO::PARAM_STR);
                    if ($registrarStmt->execute()) {
                        $registrarResult = $registrarStmt->fetch(PDO::FETCH_ASSOC);
                        if ($registrarResult) {
                            $userId = $registrarResult['id'];
                        }
                    }
                    break;
            }
            
            // Add user ID to response if found
            if ($userId !== null) {
                $response['user_id'] = $userId;
            }
            
            echo json_encode($response);
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