#!/usr/bin/python

import time
import RPi.GPIO as GPIO
from hc_sr04 import hc_sr04

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
ultrasound = hc_sr04(11,13)

print ultrasound.measure()