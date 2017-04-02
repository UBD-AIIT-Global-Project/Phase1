#coding: utf-8

import time
import sys
import os
import numpy as np
import subprocess
import pprint

def doVote(data):
        print data
        for i in range (0,3):
                ret = 0
                for j in range (0,3):
                        vote = data[i]
                        if vote == data[j]:
                                ret = ret + 1
                                j = j +1
                                if ret > 1:
                                        print "******"
                                        print data[i]
                                        print ret
                                        print "******"

                                        return vote

        # All are different values..
        datas = np.array(data)
        return np.median(datas)

def zpad(val):
        number_padded = val.zfill(8)
	print(number_padded)
	return number_padded

def getMacAd():
	cmd = "echo `ifconfig | grep 'eth0' | grep 'HWaddr' | awk '{gsub(/:/, \"\"); print $5}'`"
	return (os.popen(cmd).read())

