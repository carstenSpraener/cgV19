#!/bin/bash
(
  cd core;
  gradle clean
  gradle :cgv19-cli:installDis
)
