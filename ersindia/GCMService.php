<?php

	
class GCMService{	 
	 
	 
	function __construct() {
        
    }

public function send_notification($regID,$url){	
	 require_once 'include/config.php';
	 
 $url = 'https://android.googleapis.com/gcm/send';
 $serverApiKey = GOOGLE_API_KEY;
 $reg = $regID;
 
 
$headers = array(
 'Content-Type:application/json',
 'Authorization:key=' . $serverApiKey 
 );
 
 $data = array(
 'registration_ids' => array($reg)
 , 'data' => array(
 'type' => 'New'
 , 'title' => 'Easy Ride Share'
 , 'msg' => ''
 , 'url' => $url
 )
 );
 
 $ch = curl_init();
 curl_setopt($ch, CURLOPT_URL, $url);
 if ($headers)
 curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
 curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 curl_setopt($ch, CURLOPT_POST, true);
 curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
 
 $response = curl_exec($ch);
 
 
 // Execute post
        $response = curl_exec($ch);
        if ($response === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
curl_close($ch);
/*
if (response.getMessageId() != null) {
 String canonicalRegId = response.getCanonicalRegistrationId();
 if (canonicalRegId != null) {
   // same device has more than on registration ID: update database
 }
} else {
 String error = result.getErrorCodeName();
 if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
   // application has been removed from device - unregister database
 }
}
*/
 print ($response);
 
 }
 
}
?>