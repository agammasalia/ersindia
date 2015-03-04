<?php

if (isset($_POST['regId']) && $_POST['uid'] != ''){

$regId = $_POST['regId'];
$uid = $_POST['uid'];

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$user = $db->storeUserId($uid, $regId);
if($user){
		
		echo "Sucessfull";
		
	}else{
		echo "Fail to update";
	}	
	
}




?>