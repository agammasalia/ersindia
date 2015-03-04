<?php

$uid = $_GET['uid'];
if(isset($uid)){

	$image_name = "img_".$uid.".jpg";
	$path = 'images/'.$image_name;

	if(file_exists($path)){
		
		$im = file_get_contents($path);
		
		//header("Content-disposition: inline; filename=\"$image_name\"");
		header('Content-transfer-encoding: binary');
					
		echo $im;
		die();
	}else{
		echo "Image not found";
	}

}



?>