<!-- HTML header -->
<html>
<head>
<title>Game High Scores</title>
</head>
<body>


<!-- this PHP code gets a new score from the user and shoves it in the db -->
<?php

// get the variables we need and test that they exist
$game_index = sqlite_escape_string($_GET["game_index"]);
if($game_index === "") die("game_index not found in URL");

$seconds = sqlite_escape_string($_GET["seconds"]);
if($seconds === "") die("seconds not found in URL");

$player_name = sqlite_escape_string($_GET["player_name"]);
if($player_name === "") die("player_name not found in URL");

// open the database
$db = sqlite_open('scores.db', 0666, $error);
if (!$db) die ($error);

// build the insert string
$cmd = "insert into scores values($game_index, $seconds, \"$player_name\");";

// execute it
$ok = sqlite_exec($db, $cmd, $error);
if (!$ok) die("Cannot execute statement: $error");

// close the db
sqlite_close($db);

// give a nice message
echo "Successfully added score!";
?>

<!-- HTML footer -->
</body>
</html>

