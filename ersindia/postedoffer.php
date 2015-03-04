<?php
// array for JSON response
$response = array();

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

if(isset($_GET['gemail'])){

	$dbcon->connect();  // open db connection
	// get all offers from postoffer table of one particular email
	$email=$_GET['gemail']; 
	$result = mysql_query("SELECT * FROM postoffer where email='$email' AND date >= CURDATE() ORDER BY date");

	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		// offers node
		$response["offers"] = array();
		
		while ($row = mysql_fetch_array($result)) {
			// temp user array
			$offer = array();
			$offer["pid"] = $row["pid"];
			$offer["email"] = $row["email"];
			$offer["city"] = $row["city"];
			$offer["source"] = $row["source"];
			$offer["destination"] = $row["destination"];
			$offer["date"] = $row["date"];
			$offer["time"] = $row["time"];

			// push single offer into final response array
			array_push($response["offers"], $offer);
		}
		// success
		$response["success"] = 1;

		// echoing JSON response
		echo json_encode($response);
		
	} else {
		// no offers posted
		$response["success"] = 0;
		$response["message"] = "No Offers Posted";

		// echo no users JSON
		echo json_encode($response);
	}
	$dbcon->close();  //close db connection
}else{
	$response["success"] = 0;
	$response["message"] = "No Offers";

	// echo no users JSON
	echo json_encode($response);

}
?>