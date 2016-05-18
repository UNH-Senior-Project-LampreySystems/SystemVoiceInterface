# SystemVoiceInterface

This is the linux version of the speech interface for configuring the LNI Hub.
Note that this is just an outline of the voice interaction meant to be a proof of concept, and not a fully functioning application.

## Dependencies
This program requires the following to be installed on your linux machine:
* CMUSphinx: http://cmusphinx.sourceforge.net/
  * Add these two lines to the file cmudict-en-us.dict (located in /usr/local/share/pocketsphinx/model/en-us)
    * verbose V ER B OW S
    * verbosity V ER B AA S IH T IY

* CMUFlite: http://www.festvox.org/flite/

## Building
After installing the dependencies, run the following to build the program:
* source export.sh (this creates the environment variables used by make)
* make
