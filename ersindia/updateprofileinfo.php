<?php

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET['uid'])) {
    
    
    $uid = $_GET['uid'];
    $email = $_GET['email'];
    $name = $_GET['name'];
    $number = $_GET['phoneno'];
    $dob = $_GET['dob'];
	$gender = $_GET['gender'];
	$address = $_GET['address'];

    $dbcon->connect();  // open db connection
    // mysql update row with matched pid
    $result = mysql_query("UPDATE users SET name = '$name', number = '$number', gender = '$gender', dob = '$dob', address = '$address' , emailaddress = '$email' WHERE uid = '$uid'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Profile successfully updated.";
        
        // echoing JSON response
        echo json_encode($response);
    } else {
        
    }
	$dbcon->close();  //close db connection
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing ";

    // echoing JSON response
    echo json_encode($response);
}
?>