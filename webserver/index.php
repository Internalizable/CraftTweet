<?php

if (!empty($_GET)) {
    if (!empty($_GET['uuid'])) {
        $uuid = $_GET['uuid'];
    }
	
	if (!empty($_GET['display_name'])) {
		$display_name = $_GET['display_name'];
	}
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TwitterAuth</title>

    <!-- Bootstrap -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

</head>
<body>
<div class="container">
	<div class="row">
      <div class="col-sm-12 text-center">
        <br><br> 
		<?php if (!empty($uuid)): ?>
		
		<h2 style="color:#0fad00">Success</h2>
        <?php
                        echo '<img class="rounded mx-auto d-block" src="https://crafatar.com/renders/body/' . $uuid . '.png"/>';
						echo '<h3>Dear, <span style="color: MediumSeaGreen;">@' . $display_name . '</span></h3>';
        ?>
        <p style="font-size:20px;color:#5C5C5C;">Thank you for verifying your Twitter account with your Minecraft account. You may now exit this page.</p>
        <a href="" class="btn btn-success">     Go Home      </a>
		
		<?php else: ?> 
		<h2 style="color:#fc100d">Failure</h2>
		<img class="rounded mx-auto d-block" src="https://crafatar.com/renders/body/6ade6a8c-ced7-4204-8880-9ec02b9c478b.png"/>
        <p style="font-size:20px;color:#5C5C5C;">We could not proccess your request, please try again later.</p>
        <a href="" class="btn btn-danger">     Go Home      </a>
		
		<?php endif; ?>
		
		<br><br>
		</div>
	</div>
</div>
</body>
</html>