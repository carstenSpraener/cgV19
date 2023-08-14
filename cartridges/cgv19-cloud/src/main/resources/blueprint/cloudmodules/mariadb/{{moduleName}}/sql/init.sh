#!/bin/sh
# THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
mysqladmin -u root -prootpwd create {{moduleName}}
mysql -u root -prootpwd {{moduleName}} < /docker-entrypoint-initdb.d/init.sql
