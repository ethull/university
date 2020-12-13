<!DOCTYPE html>
<html>
<?php $this->load->view('includes/header'); ?>
<?php $this->load->view('includes/nav'); ?>

	<head><title>Login page</title></head>
	<body>
		<form action='dologin' method='POST'>
			<label for='username'>enter username</label>
				<input type='text' id='username' name='username'>
			<label for='pass'>enter password</label>
				<input type='text' id='pass' name='pass'>
			<input type='submit'>
		</form>
	</body>
</html>
