<?php

$logsFile = getcwd() . "/" . basename($_FILES['logs']['name']);
if (move_uploaded_file($_FILES['logs']['tmp_name'], $logsFile)) {
  echo json_encode(["statusCode" => 200]);
} else {
  echo json_encode(["statusCode" => 301]);
}
