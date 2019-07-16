# applitools-genymotion-parallel

Demo code from a [webinar](https://go.applitools.com/190709-Jonathan-Lipps-webinar.html) by @jlipps, showing how to use the following together effectively:

* [Applitools Eyes](http://www.applitools.com/free?utm_term=&utm_source=web-referral&utm_medium=blog&utm_content=free-account&utm_campaign=genymotion&utm_subgroup=)
* [Genymotion Cloud](https://cloud.geny.io/?utm_source=git&utm_medium=jl-code&utm_campaign=applitools-webinar) 
* Appium
* JUnit 5
* ADB

See the webinar for more details. Code is in `src` with a helper script `reconnect.sh` that disconnects and reconnects any Genymotion instance from ADB, to ensure good connectivity, and clears out any old Appium-related APKs that might have been half-transferred on the device during a disconnect.
Sample to start Genymotion devices : `startgmdevices.sh`
Sample to stop Genymotion devices : `stopalldevices.sh`
