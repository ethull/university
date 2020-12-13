<!DOCTYPE html>
<html>
<?php $this->load->view('includes/header'); ?>
<?php $this->load->view('includes/nav'); ?>

	<head><title>Post a message</title></head>
	<body>
		<form action='message/dopost' method='POST'>
		<label for='post'>post a message</label>
			<input type='text' id='post' name='post'>
			<input type='submit'>
		</form>
	</body>
</html>
