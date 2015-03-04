<?php

if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];
	
 require_once 'include/DB_Functions.php';
    $db = new DB_Functions();
  
  // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
	
	// check for tag type
    if ($tag == 'postoffer') 
	{
        // Request type is check Login
        $email = $_POST['email'];
        $city = $_POST['city'];
        $source = $_POST['source'];
        $destination = $_POST['destination'];
		$date = $_POST['date']; 
		$time = $_POST['time']; 
		$gender = $_POST['gender'];
		$type = $_POST['type'];
		$vehicle = $_POST['vehicle'];
		$vehiclenumber = $_POST['vehiclenumber'];
		$vacancy = $_POST['vacancy'];
		$fare = $_POST['fare']; 
		
		$user = $db->postUser($email, $city, $source, $destination, $date, $time, $gender, $type, $vehicle, $vehiclenumber, $vacancy, $fare);
		if ($user) 
			{
                // user stored successfully
                $response["success"] = 1;
               
//                $response["user"]["source"] = $user["source"];
//                $response["user"]["destination"] = $user["destination"];
                
                echo json_encode($response);
            } 
		else 
			{
                // user failed to store
                $response["error"] = 1;
//              $response["error_msg"] = "Error occured in post offer";
                echo json_encode($response);
            }
		
	}
  }
?>