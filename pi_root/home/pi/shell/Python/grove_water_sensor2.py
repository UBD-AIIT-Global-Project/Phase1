#!/usr/bin/env python

import time
import grovepi
import os
import sys

# Connect the Grove Water Sensor to digital port D2
# SIG,NC,VCC,GND
water_sensor = 2
grovepi.pinMode(water_sensor,"INPUT")
cnt = 0
while True:
    try:
        print (grovepi.digitalRead(water_sensor))
        time.sleep(.5)
        if grovepi.digitalRead(water_sensor) == 0:
            print ("NG")
            cnt += 0
            if cnt == 3:
              print "Warning This place fladded"
            os.system('date -R >> /home/pi/logs/water_sensor.txt')
        else:
            print ("OK")
            cnt = 0
    except IOError:
        print ("Error")
