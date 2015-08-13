#!/bin/bash
# Step 0: Edit ./no-carrier.env.list so it contains the actual GitHub login credentials

set -e -x

# set to Pacific Time (for @cvrebert)
# ln -sf /usr/share/zoneinfo/America/Los_Angeles /etc/localtime

# remove useless crap
aptitude remove wpasupplicant wireless-tools
aptitude remove pppconfig pppoeconf ppp

# setup firewall
ufw default allow outgoing
ufw default deny incoming
ufw allow ssh
ufw allow www # not necessary for NO CARRIER itself
ufw enable
ufw status verbose

# setup Docker; written against Docker v1.8.0
docker rmi no-carrier
docker build --tag no-carrier . 2>&1 | tee docker.build.log
cp ./no-carrier.env.list /etc/no-carrier.env.list
cp ./no-carrier.crontab /etc/cron.d/no-carrier
restart cron # until upstart goes away
