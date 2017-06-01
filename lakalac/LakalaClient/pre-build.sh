#!/bin/sh
build_datetime=$(date '+ %y%m%d%H%M')

#Builing Timestamp
rm -fr LakalaClient/src/main/assets/buildtime.tt
echo $build_datetime > LakalaClient/src/main/assets/buildtime.tt

