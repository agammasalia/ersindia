<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();
	
if(isset($_POST['uid']) && isset($_POST['password'])){

	$uid = $_POST['uid'];
	$password = $_POST['password'];
	
	$user = $db->changePass($uid, $password);
	if($user){
		$response["success"] = 1;
		echo json_encode($response);
	}

}else{
	echo "Invalid Request";
}
?>