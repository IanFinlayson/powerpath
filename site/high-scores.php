<!-- HTML header -->
<html>
<head>
<title>Game High Scores</title>
<style>
body { 
  color:white;
  background-color:#212421;
  text-align:center; 
} 

#r {outline: thin solid #beccb2;}
</style>
</head>
<body>
<div align="center">

<!-- start the table -->
<table cellspacing="5" cellpadding="5">
<tr><td><b>Name</b></td><td><b>Time</b></td></tr>

<!-- this PHP code queries the database to fill in the table -->
<?php

// open the database
$db = sqlite_open('scores.db', 0666, $error);
if (!$db) die ($error);

// build the SQL query
$query = "select * from scores";

// check if they requested a particular game id
$game_index = sqlite_escape_string($_GET["game_index"]);
if($game_index !== "")
  $query = $query . " where game_index = $game_index";
else
  die("Did not pass a game_index!");

// sort be seconds
$query = $query . " order by seconds";

// perform the query
$result = sqlite_query($db, $query);
if (!$result) die("Cannot execute query.");

// loop through each row returned
while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
  echo "<tr><td>";
  echo $row['player_name'];
  echo "</td><td>";
  // put the seconds into a m:s format w/ leading zeroes
  echo gmdate("i:s", $row['seconds']);
  echo "</td></tr>";
}

// close the db
sqlite_close($db);
 
?>

<!-- finish the table -->
</table>

<!-- HTML footer -->
</div>
</body>
</html>

