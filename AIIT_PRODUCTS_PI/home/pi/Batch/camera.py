# coding: utf-8

from datetime import datetime
import time
import picamera
import vote

camera = picamera.PiCamera()
timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
machineid = vote.getMacAd().rstrip("\n")
image_dir = "/home/pi/Batch/Image/"

print timestamp 
print machineid 

filename  = image_dir + machineid + ".jpg"
#filename  = image_dir + "Image.jpg"
print filename
camera.resolution = (320, 240)
camera.start_preview()
time.sleep(5)
camera.capture(filename)
camera.stop_preview()
camera.close()

