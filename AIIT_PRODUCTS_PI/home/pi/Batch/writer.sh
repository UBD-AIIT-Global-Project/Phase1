#!/bin/bash
#FileWriter Main

CODE_HED="1"
CODE_REC="2"
CODE_END="3"

NO="00000001"
KIND="000"
MAC=`ifconfig | grep 'eth0' | grep 'HWaddr' | awk '{gsub(/:/, ""); print $5}'`
AREA="01"

Logdate=`date "+%Y%m%d"`
LogName="SENSORDATA.log"
timestamp=`date "+%Y%m%d%H%M%S"`
Param=$MAC$timestamp

#BACKUP
cd /home/pi/Batch
mv $LogName bkup/$timestamp.log 

#python ./camera.py
#sleep 5s
python ./bme280.py 
python ./HC-SR04.py

echo $timestamp

T_Rec=`cat wk1.txt`$timestamp
H_REC=`cat wk2.txt`$timestamp
P_REC=`cat wk3.txt`$timestamp
U_REC=`cat wk4.txt`$timestamp
ALL_REC=$T_Rec$H_REC$P_REC$U_REC
echo $ALL_REC

HEADER=$CODE_HED$NO$KIND$MAC$AREA$timestamp
RECORD=$CODE_REC$ALL_REC
ENDREC=$CODE_END

echo $HEADER

echo $HEADER >  $LogName
echo $RECORD >> $LogName
echo $ENDREC >> $LogName
echo $UREC >> $LogName

echo $HEADER
echo $RECORD
echo $ENDREC

cd /home/pi/Batch
java FileClient $Param
#java ImageClient $Param
echo "complete"
