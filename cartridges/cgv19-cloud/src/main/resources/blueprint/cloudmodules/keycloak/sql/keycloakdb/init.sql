-- THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
create user if not exists 'keycloak'@'%' identified by '{{password}}';
grant all privileges on *.* to 'keycloak'@'%';
