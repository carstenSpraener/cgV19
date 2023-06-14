![cgv19-logo.png](images%2Fcgv19-logo.png)

# cgV19 Release 23.1

This release brings some exciting new features to cgV19

## Support for JavaPoet

You can use Java-Poet to implement your CodeGenerators for Java from a MClass-Instance.

See [the README in module core/cgv19-javapoet](../core/cgv19-javapoet/README.md)

## Introducing the new concept of CodeTargets

With CodeTargets it is possible to build reusable Generators so that a cartridge can
use the CodeGenerator of othe cartridges and enhance it. 

See 
[test example in CodeTargetModificationTest](../core/cgv19-core/src/test/java/de/spraener/nxtgen/target/CodeTargetModificationTest.java)
