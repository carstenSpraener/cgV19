#!/bin/bash
(
  cd core;
  gradle clean publish
  gradle :cgv19-cli:installDis
)
