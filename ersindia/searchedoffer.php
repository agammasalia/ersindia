<?php
// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

if(isset($_GET['date'])){
	$response = array();
	
    $dbcon->connect();  // open db connection
	
    $email=$_GET['email']; 
	$city=$_GET['city']; 
	$city="%".$city."%";
	$source=$_GET['source']; 
    $source="%".$source."%";
	$destination=$_GET['destination']; 
    $destination="%".$destination."%";
	$date=$_GET['date']; 
    $gender=$_GET['gender']; 
    $type=$_GET['type']; 
    $json_output = array();
	$result=mysql_query("SELECT * FROM postoffer where email!='$email' AND city LIKE '$city' AND source LIKE '$source' AND destination LIKE '$destination' AND date LIKE '$date' AND gender LIKE '$gender' AND type LIKE '$type' ORDER BY time") or die(mysql_error());
	
//    while($row=mysql_fetch_assoc($q))
//            $offers[]=$row;
//    print(json_encode($offers));
//    mysql_close();


	if (mysql_num_rows($result) > 0) {
		// looping through all results
		// offers node
		$response["offers"] = array();
		
		while ($row = mysql_fetch_array($result)) {
			// temp user array
			$offer = array();
			$offer["pid"] = $row["pid"];
			$offer["date"] = $row["date"];
			$offer["city"] = $row["city"];
			$offer["source"] = $row["source"];
			$offer["destination"] = $row["destination"];
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
}
?>