#!/usr/bin/python

import time
import RPi.GPIO as GPIO

class hc_sr04:
  def __init__(self,trig,echo):
    self.trig = trig
    self.echo = echo
    GPIO.setup(trig, GPIO.OUT)
    GPIO.setup(echo, GPIO.IN)
    GPIO.output(trig, GPIO.LOW)
    time.sleep(1)
        
  def measure(self):
    GPIO.output(self.trig, GPIO.HIGH)
    time.sleep(0.00001)
    GPIO.output(self.trig, GPIO.LOW)
    while GPIO.input(self.echo) == 0:
      sigoff = time.time()
    
    while GPIO.input(self.echo) == 1:
      sigon = time.time()
    
    return (sigon - sigoff) * 17000