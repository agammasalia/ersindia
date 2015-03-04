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
    $result = mysql_query("UPDATE `requests` SET `accepted`='0' WHERE `pid`='$pid' AND `requestemail`='$requestemail'");
	
	
	$result1 = mysql_query("SELECT * FROM users WHERE email = '$requestemail'");
		if (mysql_num_rows($result1) > 0) {

			while ($row = mysql_fetch_array($result1)){

					$regID = $row["gcm_regID"]; 
					$url = '3';
						
					$result2 = $gcm->send_notification($regID,$url);

					echo $result2;
					
			
			}  // end of while
		}
	$dbcon->close();  //close db connection
}
?>