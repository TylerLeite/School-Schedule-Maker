<?php
    $data = $_POST['name'];
    $fileName = '../dat/'.$data.'.tch';
    $data .= ';'.$_POST['0,0'].'~';
    $data .= $_POST['0,1'].'~';
    $data .= $_POST['0,2'].'~';
    $data .= $_POST['0,3'].'~';
    $data .= $_POST['0,4'].'~';
    $data .= $_POST['0,5'].'~';
    $data .= $_POST['0,6'].'~';
    $data .= $_POST['0,7'].'~';
    $data .= $_POST['1,0'].'~';
    $data .= $_POST['1,1'].'~';
    $data .= $_POST['1,2'].'~';
    $data .= $_POST['1,3'].'~';
    $data .= $_POST['1,4'].'~';
    $data .= $_POST['1,5'].'~';
    $data .= $_POST['1,6'].'~';
    $data .= $_POST['1,7'].'~';
    $data .= $_POST['2,0'].'~';
    $data .= $_POST['2,1'].'~';
    $data .= $_POST['2,2'].'~';
    $data .= $_POST['2,3'].'~';
    $data .= $_POST['2,4'].'~';
    $data .= $_POST['2,5'].'~';
    $data .= $_POST['2,6'].'~';
    $data .= $_POST['2,7'].'~';
    $data .= $_POST['3,0'].'~';
    $data .= $_POST['3,1'].'~';
    $data .= $_POST['3,2'].'~';
    $data .= $_POST['3,3'].'~';
    $data .= $_POST['3,4'].'~';
    $data .= $_POST['3,5'].'~';
    $data .= $_POST['3,6'].'~';
    $data .= $_POST['3,7'].'~';
    $data .= $_POST['4,0'].'~';
    $data .= $_POST['4,1'].'~';
    $data .= $_POST['4,2'].'~';
    $data .= $_POST['4,3'].'~';
    $data .= $_POST['4,4'].'~';
    $data .= $_POST['4,5'].'~';
    $data .= $_POST['4,6'].'~';
    $data .= $_POST['4,7'];
    
    
    $handle = fopen($fileName, 'w');
    fwrite($handle, $data);
    fclose($handle);
    
    header('Location:teacher.html');
?>
