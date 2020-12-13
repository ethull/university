<?php
// message controller 

defined('BASEPATH') OR exit('No direct script access allowed');
class Message extends CI_Controller {
	public function __construct(){
		parent::__construct();
		//$this->load->library('session');
		//$this->load->helper('url');
	}

	public function index() {
		if (!isset($this->session->username)) {
			//redirect('user/login');
			header("Location: user/login");
		} else {
			$this->load->view('Post');
		}
	}

	//called from post view
	public function doPost() {
		//if not logged in redirect to login
		if (!isset($this->session->username)) {
			//redirect('user/login'); for some reason redirect() doesnt work
			header("Location: user/login");
		} else {
			$this->load->model('Messages_model');
			$this->Messages_model->insertMessage($this->session->username, $_POST['post']);
			$userName=$this->session->username;
			header("Location: ../user/view/$userName");
		}
	}
}
?>
