<?php
// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET['email'])) {
    
    $email = $_GET['email'];
	
	$dbcon->connect(); // open db connection
    // mysql update row with matched pid
    $result = mysql_query("SELECT * FROM `requests` WHERE `requestemail`='$email' AND `accepted`!=0");      

    // check if row inserted or not
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // accepted node
    $response["acceptedusers"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $accept = array();
        $accept["pid"] = $row["pid"];
        $pid = $row["pid"];
        $accept["postemail"] = $row["postemail"];
		$postemail = $row["postemail"];
        $accept["requestemail"] = $row["requestemail"];
        $accept["pickup"] = $row["pickup"];
        $accept["drop"] = $row["drop"];
        $accept["accepted"] = $row["accepted"];
		$res = mysql_query("SELECT * FROM `users` WHERE `email`='$postemail'");
		$rows = mysql_fetch_array($res);
		$accept["name"] = $rows["name"];
		$accept["uid"] = $rows["uid"];
		$r = mysql_query("SELECT * FROM `postoffer` WHERE `pid`='$pid'");
		$rows = mysql_fetch_array($r);
		$accept["date"] = $rows["date"];
		$accept["time"] = $rows["time"];
		$accept["city"] = $rows["city"];
		$accept["source"] = $rows["source"];
		$accept["destination"] = $rows["destination"];

        // push single accept into final response array
        array_push($response["acceptedusers"], $accept);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
	$dbcon->close();  //close db connection
} else {
    // none accepted
    $response["success"] = 0;
    $response["message"] = "No Accepted Request";

    // echo no users JSON
    echo json_encode($response);
	}
}
?>