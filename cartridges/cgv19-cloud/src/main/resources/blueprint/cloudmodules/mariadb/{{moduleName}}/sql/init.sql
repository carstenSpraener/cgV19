-- THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
-- place initial SQL-Statements here (like creating users)
create user if not exists '{{userName}}'@'%' identified by '{{password}}';
grant all privileges on *.* to '{{userName}}'@'%';
