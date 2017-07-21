# Using a RPi 3, a Teensy, and Android things to monitor and control an aquarium

With the advent of Android Things, it becomes possible to quickly create a functional UI with a Raspberry PI. I know there are lots of ways to create UI's on the PI, but to be fair, Android would be the best way. This makes it relatively easy, even for a newb Android developer

## Why?
I like to automate things, and I want a way to see what fish are in the tank. I always forget names, so why not let the web tell everyone what's in my tank? Plus, getting data out of the tank, and controlling some aspects directly is a tantalizing concept. Android Things and the Teensy let me do this.

To be fair, standard Android would do this as well, and may be a better choice in the long run. WebView is not available yet, and I don't use PeripheralManager at all anymore, in favor of doing all the control from the Teensy.
