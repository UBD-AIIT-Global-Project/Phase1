#!/usr/bin/python
from grovepi import *
from datetime import datetime
import locale
import os.path
from time import sleep
dht_sensor_port = 7
logdir="/home/pi/shell/log/"

while True:
  today = datetime.now().strftime("%Y%m%d")
  ctime = datetime.now().strftime("%H%M")
  file_Temp = logdir + today + "_01_SENSOR.log"
  file_Hum = logdir + today + "_02_SENSOR.log"
  #f_Temp = open(file_Temp, "a")
  #f_Hum = open(file_Hum, "a")
  cnt = 0
  while True:
    cnt += 1
    try:
      if cnt == 60:
        break
      [ temp,hum ] = dht(dht_sensor_port,0)
      s_temp = "%08d" % int(temp)
      s_hum = "%08d" % int(hum)
    except:
      s_temp = "Error"
      s_hum = "Error"
    f_Temp = open(file_Temp, "a")
    f_Hum = open(file_Hum, "a")
    f_Temp.write(today + ctime + "," + s_temp + "\n") 
    f_Hum.write(today + ctime + "," + s_hum + "\n") 
    time.sleep(1)
    f_Temp.close() 
    f_Hum.close() 
