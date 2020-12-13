<!DOCTYPE html>
<html>
<!-- loads template views for header and navbar -->
<?php $this->load->view('includes/header'); ?>
<?php $this->load->view('includes/nav'); ?>

	<head><title>View Notes</title></head>
	<body>
		<?php 
			//show follow button if instructed by controller
			if (isset($showButton)) {
				if ($showButton){
					$name= $data[0]['user_username'];
					echo "<br />";
					$link=site_url("user/follow/$name");
					echo "<a href=http://$link><button>subscribe to $name </button></a>";
				}
			}
		?>
		<!-- table with all messages passed -->
		<table>
			<?php foreach ($data as $row) {?>
			<tr>
			<td><?php echo $row['user_username']; ?></td>
			<td><?php echo $row['text']; ?></td>
			<td><?php echo $row['posted_at']; ?></td>
			</tr>
			<?php } ?>
		</table>
	</body>
<?php $this->load->view('includes/footer'); ?>
</html>
