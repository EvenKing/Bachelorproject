<?php

class DB_Connect {
	private $conn;

	public function connect(){
		require_once 'Config.php';

		$this->conn = new mysqli(svr_name, svr_user, svr_pass, db_name, svr_port);

		return $this->conn;
	}
}

?>
