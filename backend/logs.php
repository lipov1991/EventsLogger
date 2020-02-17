<?php

header('Content-type: text/plain');
$logsFile = file_get_contents(getcwd() . "/logs.txt");
$logs = explode("\n", $logsFile);
foreach($logs as $log) {
   echo $log . "\n";
}
