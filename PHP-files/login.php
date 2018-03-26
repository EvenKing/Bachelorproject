<?php
require_once '../db/DB_Func.php';

$db = new DB_Func();
$response = array("error"=>FALSE);

if(isset($_POST['<navn>']) && isset($_POST['<passord>'])){
	$name = $_POST["<navn>"];
	$password = $_POST["<passord>"];

    $user = $db->getUser($name, $password);

    if($user!= false){
    	$response = array("error"=>FALSE, "error_msg"=>err_msg, "<bruker-ID>"=>$user["<bruker-ID>"], "<brukernavn>"=>$user["<brukernavn>"], "<passord>"=>$user["<passord>"], "<hendelse-ID>"=>$user["<hendelse-ID>"]);
    	if($user["<hendelse-ID>"] == null) $response["<hendelse-ID>"] = 0;
    }else {
    	$err = TRUE;
		$err_msg = "Brukernavn eller passord er feil!";
		$response = array("error"=>$err, "error_msg"=>$err_msg);
    }
}else {
	$err = TRUE;
	$err_msg = "Fyll inn brukernavn og passord!";
	$response = array("error"=>$err, "error_msg"=>$err_msg);
}
echo json_encode(array("user_data"=>$response));

?>
