<?php
// search controller

defined('BASEPATH') OR exit('No direct script access allowed');
class Search extends CI_Controller {
	public function index() {
		//display search view
		$this->load->view('Search');
	}

	//called from search view
	public function doSearch() {
		//get search string from GET parms submitted via the search-views form
		$searchString = $_GET['search'];
		$this->load->model('Messages_model');
		$data = $this->Messages_model->searchMessages($searchString);
		$data['data'] = $data;
		//view name, params
		$this->load->view('ViewMessages',$data);
	}
}
?>
