#!/bin/sh

### BEGIN INIT INFO
# Provides:          sensor03
# Required-Start:    $local_fs 
# Required-Stop:     $local_fs
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: sensor03
### END INIT INFO

. /lib/lsb/init-functions

pid=/run/sensor03.pid
dir=/home/pi/shell/Python/
script=FileCreate_03.py

case "$1" in
  start|"")
	python ${dir}${script} &
        ps -ef | grep ${script} | grep -v grep | awk '{print $2}' > ${pid}
	;;
  restart)
        kill -9 `cat ${pid}`
        sleep 1
	python ${dir}${script} &
        ps -ef | grep ${script} | grep -v grep | awk '{print $2}' > ${pid}
	;;
  stop)
        kill -9 `cat ${pid}`
	;;
esac
