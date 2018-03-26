<?php
require_once '../db/DB_Func.php';

$db = new DB_Func();
$response = array();

if(isset($_POST['userID']) && isset($_POST['prioLvl'])){
	$userid = $_POST["userID"];
	$priority = $_POST["prioLvl"];

	if($priority == 0){
		$events = $db->getEvents($userid);
	}else{
		$events = $db->getPriorityEvents($userid, $priority);
	}

	$events = json_decode($events);

	foreach($events as $jaja => $row){
		$hendel = array("<sensor-ID>"=>$row[0],"<hendelse-ID>"=>$row[1],"<kategori>"=>$row[2],"<kategori-nr>"=>$row[3],"<kategori-ID>"=>$row[4],"<kategori-rev>"=>$row[5],"<tidspunkt>"=>$row[6],"<felles-hendelse-ID>"=>$row[7],"<felles-hendelse-referanse>"=>$row[8],"<felles-hendelse-tidspunkt>"=>$row[9],"priority"=>$row[10],"status"=>$row[12],"src_ip"=>$row[13],"dst_ip"=>$row[14],"src_port"=>$row[15],"dst_port"=>$row[16],"icmp_type"=>$row[17],"icmp_code"=>$row[18],"ip_proto"=>$row[19],"ip_ver"=>$row[20],"ip_hlen"=>$row[21],"ip_tos"=>$row[22],"ip_len"=>$row[23],"ip_id"=>$row[24],"ip_flags"=>$row[25],"ip_off"=>$row[26],"ip_ttl"=>$row[27],"ip_csum"=>$row[28],"last_modified"=>$row[29],"last_uid"=>$row[30],"abuse_queue"=>$row[31],"abuse_sent"=>$row[32],"hostname"=>$row[36]);
		json_encode(array("hendelse"=>$hendel));
		$response[] = $hendel;
	}
}else{
	$response = array("error"=>TRUE);
}

echo json_encode(array("alle_hendelser"=>$response));

?>
