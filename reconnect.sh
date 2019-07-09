#!/bin/bash
set -e

instances=$(gmsaas instances list | tail -n +3 | cut -f 1 -d " ")
for i in $instances; do
    echo "Reconnecting intance $i..."
    set +e
    gmsaas instances adbdisconnect $i
    set -e
    gmsaas instances adbconnect $i
done

devices=$(adb devices | grep device | tail -n +2 | cut -f 1)
for d in $devices; do
    echo "Clearing tmp data from device $d..."
    adb -s $d shell rm -rf /data/local/tmp/appium_cache
done
