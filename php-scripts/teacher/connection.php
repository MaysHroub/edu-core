<?php

define('HOST', 'localhost');                // XAMPP runs locally
define('DB_NAME', 'school_system_db');      // the database name
define('USERNAME', 'root');                 // this is the default XAMPP MySQL user
define('PASSWORD', '');                     // XAMPP default has no password

// always use these names (because they are static) if you happen to need them somewhere which i highly doubt you will, my good ladies and gents.

try {
    $pdo = new PDO("mysql:host=" . HOST . ";dbname=" . DB_NAME . ";charset=utf8", USERNAME, PASSWORD);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    // echo "Connected successfully"; // this is an optional test line (can never be too careful, huh? :D)
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}
?>



