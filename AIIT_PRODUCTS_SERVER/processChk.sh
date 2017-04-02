#!/bin/bash

cd /home/vagrant/development/WEB/application/var/www/app

#timestamp
timestamp=`date +'%Y%m%d%H%M%S'`
echo ${timestamp} ":Start Process Check" >> PsChk.log 
echo ${timestamp} ":Start Process Check" 

#FileServer00
CHK_FileServer00=`ps -ef | grep  'FileServer00' | grep -v 'grep'`
if [ -z "${CHK_FileServer00}" ]; then
    echo $timestamp ":NO Process [FileServer00]" >> PsChk.log 
    java FileServer00 &
fi

#ImageServer00
CHK_ImageServer00=`ps -ef | grep  'ImageServer00' | grep -v 'grep'`
if [ -z "${CHK_ImageServer00}" ]; then
    echo $timestamp ":NO Process [ImageServer00]" >> PsChk.log 
    java ImageServer00 &
fi

