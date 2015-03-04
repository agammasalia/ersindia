<?php

$base=$_REQUEST['image'];
$uid=$_REQUEST['uid'];

if(isset($base) && isset($uid)){

	$image_name = "img_".$uid/*."_".date("Y-m-d-H-m-s")*/.".jpg";

	// base64 encoded utf-8 string
	$binary = base64_decode($base);

	// binary, utf-8 bytes
	 
	header("Content-Type: bitmap; charset=utf-8");
	 
	if(file_exists('images/'.$image_name)){
		unlink('images/'.$image_name);
		$file = fopen("images/" . $image_name, "wb");
		fwrite($file, $binary);
		fclose($file);
	}else{
	 
		$file = fopen("images/" . $image_name, "wb");
		 
		fwrite($file, $binary);
		 
		fclose($file);
	}

	 
	die($image_name);
 
} else {
 
	die("No POST");
}


?>