<?php
 
// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();
$response = array();
	
	if (isset($_GET['email']) && isset($_GET['emailAddress'])) {
		
		$email = $_GET['email'];
        $emailAddress = $_GET['emailAddress'];
		$dbcon->connect();  // open db connection
        $no_of_rows = mysql_num_rows(mysql_query("SELECT email from users WHERE email = '$email' AND emailaddress = 'emailAddress'"));
        
		if ($no_of_rows > 0) {
			$newpassword=sha1(rand());
			$newpassword = substr($newpassword, 0, 6);
			$salt = sha1(rand());
			$salt = substr($salt, 0, 10);
			$encrypted = base64_encode(sha1($newpassword . $salt, true) . $salt);
			$result = mysql_query("UPDATE `users` SET `encrypted_password`='$encrypted',`salt`='$salt' WHERE `email`='$email'");	
	
			if ($result) {
				// successfully updated
				$response["success"] = 1;
				$response["message"] = "Password successfully updated.";
        
				$from_add = "admin@ersindia.comeze.com"; 
				$subject = "Easy Ride Share Password Recovery";
				$headers = "From: $from_add \r\n";
				$headers .= "Reply-To: $from_add \r\n";
				$headers .= "Return-Path: $from_add\r\n";
				$headers .= "X-Mailer: PHP \r\n";
	
				if(mail($emailAddress,$subject,$newpassword,$headers)) {
					$response["success"] = 1;
					$response["message"] = "Mail Successfully Sent.";
				} else {
					$response["success"] = 0;
					$response["message"] = "Mail not sent";
				}
			} else {
				$response["success"] = 0;
				$response["message"] = "No email found";
			}
			
		} else {
            // data not set properly
            $response["success"] = 0;
			$response["message"] = "Required field(s) is missing";
        }
		$dbcon->close();  //close db connection
	}
	echo json_encode($response);
?>