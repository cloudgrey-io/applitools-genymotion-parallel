#!/bin/bash

# start Pixel XL device and connect to it with adb with serial port 1234 (--adb-serial-port optional)
startPixelXL () {
	local instance_id=$(gmsaas instances start 8e33f3db-68d9-4700-95dd-850e23d634ed pixelXL)
	gmsaas instances adbconnect $instance_id --adb-serial-port 1234
}

# start Pixel 3XL device and connect to it with adb with serial port 1234 (--adb-serial-port optional)
startPixel3XL () {
	local instance_id=$(gmsaas instances start e5008049-8394-40fc-b7f8-87fa9f1c305f pixel3XL)
	gmsaas instances adbconnect $instance_id --adb-serial-port 1235
}

#start the devices in parallel
startPixelXL &
startPixel3XL
