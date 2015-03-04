<?php

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

if (isset($_GET['pid'])){
	$pid=$_GET['pid'];

	$dbcon->connect();  // open db connection
	$result = mysql_query("SELECT * FROM postoffer where pid = '$pid'");


	// check for empty result
	if (mysql_num_rows($result) > 0) {

	   while ($row = mysql_fetch_array($result)) {
			// temp user array
		   $email = $row["email"];
		   
		   
		   $result = mysql_query("SELECT * FROM users where email = '$email'");
		 
		 
			// check for empty result
		if (mysql_num_rows($result) > 0) {

			   $response["info"] = array();
			   
			   while ($row = mysql_fetch_array($result)) {
					// temp user array
					$info = array();
					
					$d = $row["dob"];
					$age = floor((time() - strtotime($d))/31556926);
					
					
					$info["name"] = $row["name"];
					$info["dob"] = $d;
					$info["age"] = $age;
					$info["email"] = $row["emailaddress"];
					$info["gender"] = $row["gender"];
					$info["phoneno"] = $row["number"];
					$info["address"] = $row["address"];
//					$info["rating"] = $row["rating"];
					$info["uid"] = $row["uid"] ;
										
					// push single offer into final response array
				   array_push($response["info"], $info);
				}
				// success
				$response["success"] = 1;

				// echoing JSON response
				echo json_encode($response);
			} else {
				// no details found
				$response["success"] = 0;
				$response["message"] = "Details not found";

				// echo no users JSON
				echo json_encode($response);
			}

		}
	}
	$dbcon->close();  //close db connection
}

 
 if (isset($_GET['uid'])){
	$uid=$_GET['uid'];

	$dbcon->connect();  // open db connection
	$result = mysql_query("SELECT * FROM users where uid = '$uid'");

	// check for empty result
	if (mysql_num_rows($result) > 0) {

	   $response["info"] = array();
	   
	   while ($row = mysql_fetch_array($result)) {
			// temp user array
			$info = array();
			
			$d = $row["dob"];
			$age = floor((time() - strtotime($d))/31556926);
			
			
			$info["name"] = $row["name"];
			$info["dob"] = $d;
			$info["age"] = $age;
			$info["email"] = $row["emailaddress"];
			$info["gender"] = $row["gender"];
			$info["phoneno"] = $row["number"];
			$info["address"] = $row["address"];
		//	$info["profession"] = $row["profession"];
	//		$info["rating"] = $row["rating"];
			$info["uid"] = $row["uid"] ; //for image retrieval 
		 
			// push single offer into final response array
			array_push($response["info"], $info);
		}
		// success
		$response["success"] = 1;

		// echoing JSON response
		echo json_encode($response);
	} else {
		// no details found
		$response["success"] = 0;
		$response["message"] = "Details not found";

		// echo no users JSON
		echo json_encode($response);
	}

	$dbcon->close();  //close db connection
}


?>