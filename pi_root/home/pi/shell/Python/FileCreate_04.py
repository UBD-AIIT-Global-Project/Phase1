#!/usr/bin/python

from datetime import datetime
import locale
import os.path
import time
import grovepi

from  TwitterSend  import  TwitterSend
tw = TwitterSend()
logdir="/home/pi/shell/log/"

water_sensor = 2
grovepi.pinMode(water_sensor,"INPUT")

while True:
  today = datetime.now().strftime("%Y%m%d")
  ctime = datetime.now().strftime("%H%M")
  file_Water = logdir + today + "_04_SENSOR.log"
  #f_Water = open(file_Water, "w")
  cnt = 0
  con_cnt = 0 
  while True:
    cnt += 1
    if cnt == 60:
      break
    if grovepi.digitalRead(water_sensor) == 1:
      result = 0
      con_cnt = 0 
    else:
      result = 1
      con_cnt += 1
      if con_cnt == 3:
        msg = "Warning !! This place fladded. Evacuate from here ASAP"
        print msg
        tw.setMsg(msg)
        tw.sendMsg()
    distance = "%08d" % (result)
    f_Water = open(file_Water, "w")
    f_Water.write(today + ctime + "," + distance + "\n") 
    f_Water.close()
    time.sleep(1)
