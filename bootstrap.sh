#!/usr/bin/env bash

apt-get update
apt-get install -y tomcat7 tomcat7-admin
apt-get install -y postfix

mkdir -p /usr/local/scout/data
mkdir -p /usr/local/scout/plugins/adaptors
mkdir -p /usr/local/scout/plugins/notifications


chown -R tomcat7 /usr/local/scout
