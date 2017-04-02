#!/usr/bin/env python
# -*- coding: utf-8 -*-

import vote
 
def reading(sensor):
    import time
    import RPi.GPIO as GPIO
    GPIO.setwarnings(False)
     
    GPIO.setmode(GPIO.BCM)
    TRIG =20
    ECHO =16
     
    if sensor == 0:
        GPIO.setup(TRIG,GPIO.OUT)
        GPIO.setup(ECHO,GPIO.IN)
        GPIO.output(TRIG, GPIO.LOW)
        time.sleep(0.3)
         
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)
 
        while GPIO.input(ECHO) == 0:
          signaloff = time.time()
         
        while GPIO.input(ECHO) == 1:
          signalon = time.time()
 
        timepassed = signalon - signaloff
        distance = timepassed * 17000
        return distance
        GPIO.cleanup()
    else:
        print "Incorrect usonic() function varible."
         
data = []
#for i in range (0,3):

counter = 0
while counter < 3:
	wk=reading(0)
	#value=("%-6.2f"%(wk)).strip()
        if wk > 0:
	    data.append(wk)
            counter += 1

#Vote
print data
val_U1 = vote.doVote(data)
val_U1 = ("%-6.2f"%(val_U1)).strip()
#print val_U1
#print vote.zpad(val_U1)

val_U1 = vote.zpad(val_U1)
macAd = vote.getMacAd()

print "******"+macAd
print_U1="0000000104"+val_U1
#print print_U1
print_U1=print_U1.strip()

f = open('wk4.txt', 'w')
f.writelines(print_U1)
f.close()

