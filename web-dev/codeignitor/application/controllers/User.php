<?php
// user controller

defined('BASEPATH') OR exit('No direct script access allowed');
class User extends CI_Controller {
	public function __construct(){
		parent::__construct();
		$this->load->library('session');
		$this->load->helper('url');
	}
	//debug function
	//public function index() {
	//	echo "";
	//}

	// user/view/$name, view a specifc users messages
	public function view($name = null) {
		$this->load->model('Messages_model');
		$data = $this->Messages_model->getMessagesByPoster($name);
		$data['data'] = $data;
		//re-use data arr as can only pass one var
		$data['showButton'] = false;
		if (isset($this->session->username)){
			//check if following currently viewed user
			$this->load->model('Users_model');
			$data['showButton'] = !$this->Users_model->isFollowing($this->session->username, $name);
			//echo $data['showButton'] ? 'true' : 'false';
		}

		$this->load->view('ViewMessages',$data);
	}

	public function login() {
		$this->load->view('Login');
	}

	//called from login view, check if posted details correct and redirect
	public function doLogin() {
		$this->load->model('Users_model');
		$res = $this->Users_model->checkLogin($_POST['username'], $_POST['pass']);
		if ($res){
			$user_data = array('username' => $_POST['username']);
			//set username as session cookie
			$this->session->set_userdata($user_data);
			$userName=$_POST['username'];
			header("Location: view/$userName");
			//redirect("user/view/$userName");
			//return redirect()->to("user/view/$userName");
		}
		else {
			//TODO redisplay login view with error messages
			//echo "wrong login";
			$this->load->view('Login');
		}
	}

	public function logout() {
		//$this->load->library('url');
		$this->session->sess_destroy();
		header("Location: login");
	}

	public function follow($followed){
		$this->load->model('Users_model');
		$this->Users_model->follow($this->session->username, $followed);
		$link=site_url("user/view/$followed");
		header("Location: http://$link");
	}

	public function feed($followed){
		//if($followed == null) echo "enter name for feed \n";
		//else {
		//	echo "selected users feed: $followed";
		//}

		$this->load->model('Messages_model');
		$data = $this->Messages_model->getFollowedMessages($followed);
		$data['data'] = $data;

		$this->load->view('ViewMessages',$data);
	}
	
}
?>
