<?php
require_once '../db/DB_Func.php';

$db = new DB_Func();
$response = array();

$userid = $_POST["<bruker-ID>"];
$cid = $_POST["<hendelse-ID>"];
$prio = $_POST["<nivå>"];

if($prio == 0){
	$event = $db->getLatest($userid, $cid);
}else{
	$event = $db->getLatestPrio($userid, $cid, $prio);
}
$event = json_decode($event, true);

if($event != false && $event != ''){
	if($cid == $event[0][<tall>]){
		$response = array("updated"=>FALSE, "error"=>FALSE);
	}else{
		foreach($event as $jaja => $row){
			$hendel = array("<sensor-ID>"=>$row[<tall>],"<hendelse-ID>"=>$row[<tall>],"<type>"=>$row[<tall>],"<tidspunkt>"=>$row[<tall>],"<nivå>"=>$row[<tall>],"<sensornavn>"=>$row[<tall>]);
			json_encode(array("hendelse"=>$hendel));
			$response[] = $hendel;
		}
	}
}else{
	$response = array("updated"=>FALSE, "error"=>TRUE);
}
echo json_encode(array("oppdatert"=>$response));

?>
