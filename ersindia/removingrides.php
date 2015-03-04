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
    // mysql delete row with matched pid
    $result = mysql_query("DELETE FROM `requests` WHERE `pid`='$pid' AND `requestemail`='$requestemail'");
	
	$ress = mysql_query("SELECT * FROM users where email = '$requestemail'");
	if (mysql_num_rows($ress) > 0){
	
		while ($row = mysql_fetch_array($ress)){

				$regID = $row["gcm_regID"]; 
				$url = '2';
				$result2 = $gcm->send_notification($regID,$url);
				echo $result2;
				
		}  // end of while	
	}
	$dbcon->close();  //close db connection
}
?>