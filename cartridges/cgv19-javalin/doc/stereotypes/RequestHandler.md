
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "RequestHandler"

A method marked with this stereotype will be translated to a handler
class that receives the context from JavaLin and has a method to place
the business logic.

Each request will create a new instance of this request handler.

In the tagged values of the requst handler stereotype you can specify the requests parameters like path and method.


## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element


## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__Metod__| adhoc-enum | There is no documentation yet.<br/><br/>_Default Value:_ 'GET'<br/><br/>__Allowed values:__<br/>* 'GET'<br/>* 'POST'<br/>* 'PUT'<br/>* 'DELETE'<br/>* 'PATCH'<br/> |
|__path__| String | There is no documentation yet.<br/><br/>_Default Value:_ '/' |

