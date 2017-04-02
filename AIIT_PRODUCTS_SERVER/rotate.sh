#!/bin/bash
cd /home/vagrant/development/WEB/application/var/www/app
find . -name '*.jpg' -type f -print0 | xargs -0 -I% cp % Old/%_`date +'%Y%m%d%H%M'`
