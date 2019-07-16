#!/bin/bash

#stop all instances when tests are finished
gmsaas instances list | awk 'NR > 2 { print $1 }' | while read uuid ; do
	gmsaas instances stop $uuid &
done
