all: clean main-activity

main-activity:
	gcc -g -o main-activity main-activity.c speechutils.c systemutils.c internetutils.c -DMODELDIR=\"`pkg-config --variable=modeldir pocketsphinx`\" `pkg-config --cflags --libs pocketsphinx sphinxbase` -I$(FLITEDIR)/include -L$(FLITEDIR)/lib -lflite_cmu_us_kal -lflite_usenglish -lflite_cmulex -lflite -lm

clean:
	-rm main-activity
