<?php

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

// array for JSON response
$response = array();
include_once 'GCMService.php';
$gcm = new GCMService();

// check for required fields
if (isset($_GET['pid'])) {
	$pid = $_GET['pid'];
	
		$dbcon->connect();  // open db connection
		$res = mysql_query("SELECT * FROM requests WHERE pid = $pid");
		
		if (mysql_num_rows($res) > 0) {

		   while ($row = mysql_fetch_array($res)) {
				
			   $email = $row["requestemail"];
			   
			   $ress = mysql_query("SELECT * FROM users where email = '$email'");
					// check for empty result
				if (mysql_num_rows($ress) > 0){
				
						while ($row = mysql_fetch_array($ress)){
				
								$regID = $row["gcm_regID"]; 
								$url = '2';
								$result2 = $gcm->send_notification($regID,$url);
								echo $result2;
								
						}  // end of while	
				}
			}
			
		}
		// mysql update row with matched pid
		$result = mysql_query("DELETE FROM postoffer WHERE pid = $pid");
		
		// check if row deleted or not
		if (mysql_affected_rows() > 0) {
			// successfully updated
			$response["success"] = 1;
			$response["message"] = "Offer successfully deleted";

			// echoing JSON response
			echo json_encode($response);
				
		} else {
			// no product found
			$response["success"] = 0;
			$response["message"] = "No Offer found";

			// echo no users JSON
			echo json_encode($response);
		}
		
		$dbcon->close();  //close db connection
} else {
		// required field is missing
		$response["success"] = 0;
		$response["message"] = "Required field(s) is missing";

		// echoing JSON response
		echo json_encode($response);
}
?>