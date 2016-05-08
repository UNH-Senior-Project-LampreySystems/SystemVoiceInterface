all: main-activity

main-activity:
	gcc -o main-activity main-activity.c -DMODELDIR=\"`pkg-config --variable=modeldir pocketsphinx`\" `pkg-config --cflags --libs pocketsphinx sphinxbase`
