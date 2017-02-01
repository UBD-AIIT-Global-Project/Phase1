#!/usr/bin/python

from datetime import datetime
import locale
import os.path
import time
import picamera
import FileUpload_05

logdir="/home/pi/shell/log/"

while True:
  #today = datetime.now().strftime("%Y%m%d")
  #ctime = datetime.now().strftime("%H%M")
  file_Image = logdir + "05_SENSOR.jpg"
  with picamera.PiCamera() as camera:
    camera.resolution = (1024, 768)
    camera.start_preview()
    time.sleep(2)
    camera.capture(file_Image)
  FileUpload_05.FileUpload()
  cnt = 0
  while True:
    cnt += 1
    if cnt == 300:
      break
    time.sleep(1)
