#!/bin/bash

for m in core cartridges; do
  (
    cd $m;
    gradle clean publish
  )
done

(cd core; gradle :cgv19-cli:installDist)

rm -rf ~/Programme/cgv19/*
cp -r ./core/cgv19-cli/build/install/cgv19-cli/ ~/Programme/cgv19
for c in angular cloud javalin restcartridge; do
  cp cartridges/cgv19-$c/build/libs/cgv19*.jar ~/Programme/cgv19/cartridges
done
