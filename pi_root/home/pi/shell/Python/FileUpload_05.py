#!/usr/bin/python
from datetime import datetime
from datetime import timedelta
import locale
import os.path
import time
import subprocess
import commands

def FileUpload():
  type="05"
  mac=commands.getoutput("/sbin/ifconfig eth0 | grep HWaddr | awk '{print $NF}' | sed -e 's/://g'")
  logdir="/home/pi/shell/log/"
  now = datetime.now()
  today = now.strftime("%Y%m%d")
  ctime = datetime.now().strftime("%H%M")
  file_Log= logdir + "FileUpload_" + today + "_" + type + ".log"
  file_Image = logdir + "05_SENSOR.jpg"
  file_Upload = mac + "_" + type + "_Upload.jpg"
  f_Log = open(file_Log, "w")

  cmd = "/usr/local/bin/aws s3 cp " + file_Image + " s3://enpit2015-sensors/" + type + "/" + file_Upload + " --profile=enpit2015 --region=us-west-2"
  print cmd
  subprocess.call( cmd, shell=True )
  f_Log.write(file_Upload + " uploaded to S3\n")
  f_Log.close()
