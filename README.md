# SystemVoiceInterface

This is the linux version of the speech interface for configuring the LNI Hub.
Note that this is just an outline of the voice interaction meant to be a proof of concept, and not a fully functioning application.

## Dependencies
This program requires the following to be installed on your linux machine:
* CMUSphinx: http://cmusphinx.sourceforge.net/
* CMUFlite: http://www.festvox.org/flite/

## Building
After installing the dependencies, run the following to build the program:
* source export.sh (this creates the environment variables used by make)
* make
