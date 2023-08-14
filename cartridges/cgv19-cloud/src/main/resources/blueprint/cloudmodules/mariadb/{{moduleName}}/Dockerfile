# THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
FROM {{dockerImage}}

COPY mariadb-{{moduleName}}.cnf /etc/mysql/conf.d/
COPY sql/* /docker-entrypoint-initdb.d
