# Gate Control

I'm just sharing my little home project to control my gate.

Using this i am able to open and close my gate from anywhere using my android phone or a regular web browser.
Furthermore i can always check if i forgot to close my gate and i get a notification whenever my gate is opening or closing.

## Hardware

Everything is running on a raspberry pi which i build into my gate motor.

The motor of my gate has a controlboard which has a simple input to open and close the gate,
as well as an output which normally is used to trigger a signal lamp when the gate is open.

[Motor Datasheet](http://www.faacusa.com/uploads/media/746___780D.pdf)

The input to open and close the gate is called "Open A" (Pin 1) and the output is labled "W.L." (Pin 12).

The motor is programmed in stepped semi-automatic mode (EP) and the indicator light has the traffic lights (E4) function active.

The controlboard is using 24V so the pins cannot be connected directly to the raspberry pi.

Therefore i build a simple interface board using two optocouplers:

![Screenshot](http://i.imgur.com/SNWqdcQ.png)

![Screenshot](http://i.imgur.com/vTaCdtxl.jpg)

This is how everything looks like build into my motor:

![Screenshot](http://i.imgur.com/ewkCJkVl.jpg)

## Software

The raspberry pi runs a simple node js server which uses a [Express](http://expressjs.com/) to host the webpage and provide the REST api to control the gate.

To get real time updates about the opening and closing gate, the webpage uses [socket.io](http://socket.io/).

The android application is using [Google Cloud Messaging](https://developers.google.com/cloud-messaging/) to receive changes of the gate state.
