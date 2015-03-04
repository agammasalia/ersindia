<?php


// array for JSON response
$response = array();

// connecting to db
require_once 'include/DB_Connect.php';
$dbcon = new DB_Connect();

// check for post data
if (isset($_GET["pid"])) {
    $pid = $_GET["pid"];

	$dbcon->connect();  // open db connection
    // get a offer from postoffer table
    $result = mysql_query("SELECT * FROM postoffer WHERE pid = $pid");

    if (!empty($result)) {
        // check for empty result
		if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);

            $offer = array();
			$offer["pid"] = $result["pid"];
			$offer["email"] = $result["email"];
			$offer["city"] = $result["city"];
			$offer["source"] = $result["source"];
			$offer["destination"] = $result["destination"];
			$offer["date"] = $result["date"];
			$offer["time"] = $result["time"];
			$offer["gender"] = $result["gender"];
			$offer["type"] = $result["type"];
			$offer["vehicle"] = $result["vehicle"];
			$offer["vehiclenumber"] = $result["vehiclenumber"];
			$offer["vacancy"] = $result["vacancy"];
			$offer["fare"] = $result["fare"];
            
			// success
            $response["success"] = 1;

            // user node
            $response["offer"] = array();

            array_push($response["offer"], $offer);

            // echoing JSON response
            echo json_encode($response);
			
        } else {
            // no offer found
            $response["success"] = 0;
            $response["message"] = "No Offer Found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no offer found
        $response["success"] = 0;
        $response["message"] = "No Offer Found";

        // echo no users JSON
        echo json_encode($response);
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