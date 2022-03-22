#cgV19 OOM
This module implements a very basic Object Oriented Model
for cgV19.

##MetaModel
The Meta-Model has MPackage, MClass, MReference and MAttribute to 
build class models. Relations like _implements_ or _extends_ 
are projected on a MClassRef with Stereotype << Implements >> or
<< Extends >>.

See java-doc of the several classes.

##The Model Loader
The OOMModelLoader can handle oom-Files or URLs to the
EndPoint of the MagicDraw Plugin.

