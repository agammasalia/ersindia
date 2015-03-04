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
    $email = $_GET['email'];
    $city = $_GET['city'];
    $source = $_GET['source'];
    $destination = $_GET['destination'];
	$date = $_GET['date']; 
	$time = $_GET['time']; 
	$gender = $_GET['gender'];
	$type = $_GET['type'];
	$vehicle = $_GET['vehicle'];
	$vehiclenumber = $_GET['vehiclenumber'];
	$vacancy = $_GET['vacancy'];
	$fare = $_GET['fare']; 
	
	$dbcon->connect();  // open db connection
    
    // mysql update row with matched pid
    $result = mysql_query("UPDATE postoffer SET email = '$email', city = '$city', source = '$source', destination = '$destination', date = '$date', time = '$time', gender = '$gender', type = '$type', vehicle = '$vehicle', vehiclenumber = '$vehiclenumber', vacancy = '$vacancy', fare = '$fare' WHERE pid = '$pid'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Product successfully updated.";
        
        // echoing JSON response
        echo json_encode($response);
		
		$res = mysql_query("SELECT * FROM requests WHERE pid = '$pid'");
				
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
		
    } else {
        
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