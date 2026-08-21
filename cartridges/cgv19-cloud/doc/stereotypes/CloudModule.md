
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "CloudModule"

A package marked with this Stereotype will describe all deployment components in a component diagram.

If the CloudModule requires a database, a transformation will create a
deployment, service and a PVC for that database.

At the moment the only supported database type is MariaDB. More to come


## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element


## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__dockerRegistry__| String | There is no documentation yet.<br/><br/>_Default Value:_ 'localhost:5080' |
|__cgv19Cartridge__| String | There is no documentation yet.<br/><br/>_Default Value:_ 'REST-Cartridge' |
|__dockerImage__| String | There is no documentation yet. |
|__port__| integer | There is no documentation yet.<br/><br/>_Default Value:_ '8080' |
|__requiredDatabase__| adhoc-enum | There is no documentation yet.<br/><br/>_Default Value:_ 'none'<br/><br/>__Allowed values:__<br/>* 'none'<br/>* 'PostgreSQL'<br/>* 'MariaDB'<br/>* 'MySQL'<br/>* 'DB/2'<br/>* 'HSQL'<br/>* 'Other'<br/> |

