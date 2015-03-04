<?php

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();
// array for JSON response
$response = array();

include_once 'GCMService.php';
$gcm = new GCMService();

// check for required fields
if (isset($_GET['pid']) && isset($_GET['requestemail'])){
		
		$pid = $_GET['pid'];
		$postemail = $_GET['postemail'];
		$requestemail = $_GET['requestemail'];
		$pickup = $_GET['pickup'];
		$drop = $_GET['drop']; 
		
		$dbcon->connect();  // open db connection
		// mysql update row with matched pid
		
		$res = mysql_query("SELECT * FROM requests WHERE pid='$pid' AND requestemail='$requestemail'");
		if(mysql_num_rows($res)!= 0 ) {
				$response["success"] = 2;
				$response["message"] = "All Ready Requested.";
				echo json_encode($response);
		} else{
		
			
			$result = mysql_query("INSERT INTO `requests`(`pid`, `postemail`, `requestemail`, `pickup`, `drop`) VALUES ('$pid', '$postemail', '$requestemail', '$pickup', '$drop')");

			// check if row inserted or not
			if ($result){
				// successfully updated
				$response["success"] = 1;
				$response["message"] = "Requested successfully.";
					   
				// echoing JSON response
				echo json_encode($response);
				
				$result1 = mysql_query("SELECT * FROM users WHERE email = '$postemail'");
				if (mysql_num_rows($result1) > 0) {

					while ($row = mysql_fetch_array($result1)){
			
							$regID = $row["gcm_regID"]; 
							$url = '1';
								
							$result2 = $gcm->send_notification($regID,$url);

							echo $result2;
							
					}  // end of while

				}
			}else {
				// required field is missing
				$response["success"] = 0;
				$response["message"] = "Required field(s) is missing";

				// echoing JSON response
				echo json_encode($response);
			}
		
		}
	$dbcon->close();  //close db connection
}
?>