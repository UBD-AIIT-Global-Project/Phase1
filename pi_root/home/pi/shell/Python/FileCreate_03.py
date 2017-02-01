#!/usr/bin/python

from datetime import datetime
import locale
import os.path
import time

import RPi.GPIO as GPIO
from hc_sr04 import hc_sr04

from  TwitterSend  import  TwitterSend
tw = TwitterSend()

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
ultrasound = hc_sr04(11,13)
logdir="/home/pi/shell/log/"
file_Threshold = logdir + "threshold_03"
f_Threshold = open(file_Threshold,"r")
for line in f_Threshold:
  threshold = line

while True:
  today = datetime.now().strftime("%Y%m%d")
  ctime = datetime.now().strftime("%H%M")
  file_Usonic = logdir + today + "_03_SENSOR.log"
  #f_Usonic = open(file_Usonic, "a")
  cnt = 0
  while True:
    cnt += 1
    if cnt == 60:
      break
    distance = ultrasound.measure()
    distance = "%08d" % (distance)
    print distance
    f_Usonic = open(file_Usonic, "a")
    f_Usonic.write(today + ctime + "," + distance + "\n") 
    f_Usonic.close()
    if distance <= threshold:
      try:
        msg = today + " " + ctime + " " + str(cnt) + " Warning !! This place floodded. Evacuate from here ASAP"
        print msg
        tw.setMsg(msg)
        tw.sendMsg()
      except:
        print "twitter error" 

    time.sleep(1)
