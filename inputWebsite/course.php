<?php
    $data = $_POST['name'];
    $fileName = '../dat/'.$data.'.dat';
    $data .= ';'.$_POST['freq'].';';
    $data .= $_POST['teacher'].';';
    $data .= $_POST['student1'].'~';
    $data .= $_POST['student2'].'~';
    $data .= $_POST['student3'].'~';
    $data .= $_POST['student4'].'~';
    $data .= $_POST['student5'].'~';
    $data .= $_POST['student6'].'~';
    $data .= $_POST['student7'].'~';
    $data .= $_POST['student8'].'~';
    $data .= $_POST['student9'].'~';
    $data .= $_POST['student10'].'~';
    $data .= $_POST['student11'].'~';
    $data .= $_POST['student12'].'~';
    $data .= $_POST['student13'].'~';
    $data .= $_POST['student14'].'~';
    $data .= $_POST['student15'].'~';
    $data .= $_POST['student16'].'~';
    $data .= $_POST['student17'].'~';
    $data .= $_POST['student18'].'~';
    $data .= $_POST['student19'].'~';
    $data .= $_POST['student20'].'~';
    $data .= $_POST['student21'].'~';
    $data .= $_POST['student22'].'~';
    $data .= $_POST['student23'].'~';
    $data .= $_POST['student24'].'~';
    $data .= $_POST['student25'].'~';
    $data .= $_POST['student26'].'~';
    $data .= $_POST['student27'].'~';
    $data .= $_POST['student28'].'~';
    $data .= $_POST['student29'].'~';
    $data .= $_POST['student30'].';';
    $data .= $_POST['1A'].'~';
    $data .= $_POST['1B'].'~';
    $data .= $_POST['1C'].'~';
    $data .= $_POST['NO'].'~';
    $data .= $_POST['DS'].'~';
    $data .= $_POST['RL'].'~';
    $data .= $_POST['LL'].'~';
    $data .= $_POST['BF'].'~';
    $data .= $_POST['2A'].'~';
    $data .= $_POST['2B'].'~';
    $data .= $_POST['2C'].'~';
    $data .= $_POST['2D'].'~';
    $data .= $_POST['2E'].'~';
    $data .= $_POST['MR'].'~';
    $data .= $_POST['EO'].';';
    
    $handle = fopen($fileName, 'w');
    fwrite($handle, $data);
    fclose($handle);
    
    header('Location:course.html');
?>
