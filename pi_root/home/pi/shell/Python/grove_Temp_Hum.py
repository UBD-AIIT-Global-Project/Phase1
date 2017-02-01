#!/usr/bin/python

# grovepi_Temp_Hum.py
from grovepi import *
# Connect the DHt sensor to port 7
dht_sensor_port = 7
while True:
  try:
    #Get the temperature and Humidity from the DHT sensor
    [ temp,hum ] = dht(dht_sensor_port,1)
    print "temp =", temp, "C\thumadity =", hum,"%"
    t = str(temp)
    h = str(hum)
    print "Temp:" + t + "C " + "Humidity :" + h + "%"
  except (TypeError) as e:
    print "Error"
