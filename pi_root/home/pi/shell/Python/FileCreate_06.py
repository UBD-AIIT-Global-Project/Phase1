#!/usr/bin/python

from datetime import datetime
import locale
import os.path
import time

from grovepi import *

from  TwitterSend  import  TwitterSend
tw = TwitterSend()

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
ultrasonic_ranger = 4
logdir="/home/pi/shell/log/"
file_Threshold = logdir + "threshold_06"
f_Threshold = open(file_Threshold,"r")
for line in f_Threshold:
  threshold = int(line)
print threshold

while True:
  today = datetime.now().strftime("%Y%m%d")
  today2 = datetime.now().strftime("%Y/%m/%d")
  ctime = datetime.now().strftime("%H%M")
  ctime2 = datetime.now().strftime("%H:%M")
  file_Usonic = logdir + today + "_06_SENSOR.log"
  #f_Usonic = open(file_Usonic, "a")
  cnt = 0
  cnt2 = 0
  while True:
    cnt += 1
    if cnt == 60:
      break
    try:
      distance1 = ultrasonicRead(ultrasonic_ranger)
    except:
      distance1 = 999
    distance2 = "%08d" % (distance1)
    print distance1
    f_Usonic = open(file_Usonic, "a")
    f_Usonic.write(today + ctime + "," + distance2 + "\n") 
    f_Usonic.close()
    if distance1:
     if distance1 <= threshold:
      cnt2 = cnt2 + 1
      if (cnt2 == 3) or ((cnt2 > 3) and (cnt2 % 30 == 0)):
        try:
          msg = today2 + " " + ctime2 + " " + str(cnt) + " Warning !! AIIT is floodded. Evacuate from here ASAP"
          print msg
          tw.setMsg(msg)
          tw.sendMsg()
        except:
          print "twitter error" 
     else:
      cnt2 = 0

    time.sleep(1)
