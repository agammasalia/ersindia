<?php

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();
include_once 'GCMService.php';
$gcm = new GCMService();

// check for required fields
if (isset($_GET['pid']) AND isset($_GET['requestemail'])) {
    
    $pid = $_GET['pid'];
	$requestemail = $_GET['requestemail'];
	
	$dbcon->connect();  // open db connection
   
   // mysql update row with matched pid
   
	$vres = mysql_query("SELECT * FROM `postoffer` WHERE `pid`='$pid'");
	$vacancyrow = mysql_fetch_array($vres);
	$vacmax = $vacancyrow["vacancy"];
	$vres1 = mysql_query("SELECT * FROM `requests` WHERE pid='$pid' AND `accepted`='1'");
	$vaccount = mysql_num_rows($vres1);
		
	if($vaccount<$vacmax){
		
		$result = mysql_query("UPDATE `requests` SET `accepted`='1' WHERE `pid`='$pid' AND `requestemail`='$requestemail'");
	
		$result1 = mysql_query("SELECT * FROM users WHERE email = '$requestemail'");
				if (mysql_num_rows($result1) > 0) {

					while ($row = mysql_fetch_array($result1)){
			
							$regID = $row["gcm_regID"]; 
							$url = '2';
								
							$result2 = $gcm->send_notification($regID,$url);

							echo $result2;
							
					}  // end of while
				}
	}else{
	
		$response["success"] = 0;

		// echoing JSON response
		echo json_encode($response);
	}
		$dbcon->close();  //close db connection
}
?>