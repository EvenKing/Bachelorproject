<?php

class DB_Func {

	private $conn;

	function __construct() {
		require_once 'DB_Connect.php';

		$db = new DB_Connect();
		$this->conn = $db->connect();
	}

	function __destruct() { }

	public function getUser($name, $password){

		$stmt = $this->conn->prepare("SELECT <brukertabell>.*, <hendelsestabell>.<hendelse-ID> FROM <brukertabell>, <adgangstabell> LEFT JOIN <hendelsestabell> ON <hendelsestabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> WHERE <adgangstabell>.<bruker-ID> = <brukertabell>.<bruker-ID> AND <brukertabell>.<brukernavn> = $name ORDER BY <hendelsestabell>.<tidspunkt> DESC LIMIT 1;");

		if($stmt->execute()){
			$user = $stmt->get_result()->fetch_assoc();
			$stmt->close();

			$krypt_pass = $user['<passord>'];
			$hash = $this->sjekkhash($password);

			if($krypt_pass == $hash){
				return $user;
			}
		}else {
			return NULL;
		}
	}

	public function getEvents($userid){

		$stmt = $this->conn->prepare("SELECT * FROM <hendelsestabell> LEFT JOIN <adgangstabell> ON <hendelsestabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> LEFT JOIN <sensortabell> ON <sensortabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> WHERE <adgangstabell>.<bruker-ID> = $userid AND <hendelsestabell>.<nivå> BETWEEN 1 AND 3 ORDER BY <tidspunkt> DESC LIMIT 50;");

		if($stmt->execute()){
			$respo = $stmt->get_result()->fetch_all();
			$stmt->close();
			return json_encode($respo);
		}else{
			return NULL;
		}
	}

	public function getPriorityEvents($userid, $prio){

		$stmt = $this->conn->prepare("SELECT * FROM <hendelsestabell> LEFT JOIN <adgangstabell> ON <hendelsestabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> LEFT JOIN <sensortabell> ON <sensortabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> WHERE <adgangstabell>.<bruker-ID> = $userid AND <hendelsestabell>.<nivå> = $prio ORDER BY <tidspunkt> DESC LIMIT 25;");

		if($stmt->execute()){
			$respo = $stmt->get_result()->fetch_all();
			$stmt->close();
			return json_encode($respo);
		}else{
			return NULL;
		}
	}

	public function getLatest($userid, $eventid){

		$stmt = $this->conn->prepare("SELECT * FROM <hendelsestabell> LEFT JOIN <adgangstabell> ON <hendelsestabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> LEFT JOIN sensor ON sensor.<sensor-ID> = <adgangstabell>.<sensor-ID> WHERE <adgangstabell>.<bruker-ID> = $userid AND <hendelsestabell>.<hendelse-ID> >= $eventid AND <hendelsestabell>.<nivå> BETWEEN 1 AND 3 ORDER BY <tidspunkt> DESC;");

		if($stmt->execute()){
			$respo = $stmt->get_result()->fetch_all();
			$stmt->close();
			return json_encode($respo);
		}else{
			return NULL;
		}
	}

	public function getLatestPrio($userid, $eventid, $lvl){

		$stmt = $this->conn->prepare("SELECT * FROM <hendelsestabell> LEFT JOIN <adgangstabell> ON <hendelsestabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> LEFT JOIN <sensortabell> ON <sensortabell>.<sensor-ID> = <adgangstabell>.<sensor-ID> WHERE <adgangstabell>.<bruker-ID> = $userid AND <hendelsestabell>.<hendelses-ID> >= $eventid AND <hendelsestabell>.<nivå> = $lvl ORDER BY <tidspunkt> DESC;");

		if($stmt->execute()){
			$respo = $stmt->get_result()->fetch_all();
			$stmt->close();
			return json_encode($respo);
		}else{
			return NULL;
		}
	}

	public function hashe($passord) {
		$salt1 = hash('sha256', '<salt-ord-1>');
		$salt2 = hash('sha256', '<salt-ord-2>');
		$kryptert = hash('sha256', <salt og passord en rekkefølge>);
		$hash = array("kryptert"=> $kryptert);
		return $hash;
	}

	public function sjekkhash($passord){
		$salt1 = hash('sha256', '<salt-ord-1>');
		$salt2 = hash('sha256', '<salt-ord-2>');
		$hash = hash('sha256', <salt og passord i en rekkefølge>);
		return $hash;
	}
}

?>
