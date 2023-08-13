#!/bin/sh
# THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS

mysqladmin -prootpwd create keycloakdb

mysql -prootpwd keycloakdb <./init.sql
