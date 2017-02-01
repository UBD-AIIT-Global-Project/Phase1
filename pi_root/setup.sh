#!/bin/sh

apt-get -y update
apt-get -y upgrade

cd /home/pi
curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py"
python get-pip.py
pip install awscli

cd /home/pi
wget https://pypi.python.org/packages/source/s/simplejson/simplejson-3.8.1.tar.gz
tar -xvzf simplejson*.tar.gz
cd simplejson*
python setup.py install

cd /home/pi
wget https://pypi.python.org/packages/source/r/requests/requests-2.9.0.tar.gz
tar -xvzf requests-2.9.0.tar.gz
cd requests-2.9.0
python setup.py install

cd /home/pi
wget https://pypi.python.org/packages/source/r/requests-oauthlib/requests-oauthlib-0.6.0.tar.gz
tar -xvzf requests-oauthlib-0.6.0.tar.gz
cd requests-oauthlib-0.6.0
python setup.py install
 
cd /home/pi
wget https://pypi.python.org/packages/source/s/setuptools/setuptools-19.1.1.tar.gz
tar -xvzf setuptools-19.1.1.tar.gz
cd setuptools*
python setup.py install

cd /home/pi
wget https://pypi.python.org/packages/source/o/oauthlib/oauthlib-1.0.3.tar.gz
tar -xvzf oauthlib-1.0.3.tar.gz
cd oauthlib-*
python setup.py install

cd /home/pi
wget https://python-twitter.googlecode.com/files/python-twitter-1.1.tar.gz
tar -xvzf python-twitter-1.1.tar.gz
cd python-twitter-*
python setup.py install

apt-get -y install chkconfig
apt-get -y install i2c-tools python-smbus
apt-get -y install tightvncserver

mkdir -p /home/pi/shell/Python /home/pi/shell/log
cp -Rp /home/pi/pi_root/* /

update-rc.d vncboot defaults
update-rc.d sensor01_02.sh defaults
update-rc.d sensor03.sh defaults
update-rc.d sensor04.sh defaults
update-rc.d sensor05.sh defaults
update-rc.d sensor06.sh defaults

crontab /var/spool/cron/crontabs/root
