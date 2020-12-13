<!DOCTYPE html>
<html>
<?php $this->load->view('includes/header'); ?>
<?php $this->load->view('includes/nav'); ?>

	<head><title>Search page</title></head>
	<body>
		<form action='search/dosearch' method='GET'>
		<label for='search'>Search a persons messages</label>
			<input type='text' id='search' name='search'>
			<input type='submit'>
		</form>
	</body>
</html>
