#include <pocketsphinx.h>
#include <ps_search.h>
#include <string.h>
#include <assert.h>
#include <sys/select.h>
#include <sphinxbase/err.h>
#include <sphinxbase/ad.h>
#include <sphinxbase/fsg_model.h>
#include <sphinxbase/ngram_model.h>

static const char KWS[] = "key word search";
static const char KEY_PHRASE[] = "wake";
static const char GRS[] = "grammer search";
static const char GRAMMAR_PATH[] = "menu.gram";

ps_decoder_t *ps = NULL;	
cmd_ln_t *config = NULL;

void sleep_msec(int32 ms)
{
	struct timeval tmo;
	tmo.tv_sec = 0;
	tmo.tv_usec = ms * 1000;
	select(0, NULL, NULL, NULL, &tmo);
}

void recognize_from_microphone()
{
	ad_rec_t *ad;
	int16 adbuf[2048];
	uint8 utt_started, in_speech;
	int32 k;
	char const *hyp;

	if((ad = ad_open_dev(cmd_ln_str_r(config, "-adcdev"),
			(int) cmd_ln_float32_r(config, "-samprate"
						))) == NULL)
		E_FATAL("Failed to open audio device\n");
	if(ad_start_rec(ad) < 0)
		E_FATAL("Failed to start recording\n");
	
	if(ps_start_utt(ps) < 0)
		E_FATAL("Failed to start utterance\n");
	utt_started = FALSE;
	E_INFO("Ready...");

	for(;;)
	{
		if((k = ad_read(ad, adbuf, 2048)) < 0)
			E_FATAL("Failed to read audio\n");
		ps_process_raw(ps, adbuf, k, FALSE, FALSE);
		in_speech = ps_get_in_speech(ps);
		if(in_speech && !utt_started)
		{
			utt_started = TRUE;
			E_INFO("Listening...\n");
		}
		if(!in_speech && utt_started)
		{
			ps_end_utt(ps);
			hyp = ps_get_hyp(ps, NULL);
			if(hyp != NULL)
			{
				if(strcmp(hyp, KEY_PHRASE) == 0)
					ps_set_search(ps, GRS);
				printf("%s\n", ps_get_search(ps));
				printf("%s\n", hyp);
				fflush(stdout);
			}

			if(ps_start_utt(ps) < 0)
				E_FATAL("Failed to start utterance\n");
			utt_started = FALSE;
			E_INFO("Ready...\n");
		}
		sleep_msec(150);
	}
	ad_close(ad);
}



int
main(int argc, char *argv[])
{	
	/* Create the Configuration */
	config = cmd_ln_init(NULL, ps_args(), TRUE,
		"-hmm", MODELDIR "/en-us/en-us",
		"-lm", MODELDIR "/en-us/en-us.lm.bin",
		"-dict", MODELDIR "/en-us/cmudict-en-us.dict",
		NULL);
	if(config == NULL)
	{
		fprintf(stderr, "\nFAILED TO CREATE CONFIGURATION\n");
		return -1;
	}

	/* Create the Recognizer from the configuration*/
	ps = ps_init(config);
	if(ps == NULL)
	{
		fprintf(stderr, "\nFAILED TO CREATE RECOGNIZER\n");
		return -1;
	}

	/* Setup the keyword search */
	ps_set_keyphrase(ps, KWS, KEY_PHRASE);
	ps_set_search(ps, KWS); 

	/* Setup the grammar search */
	ps_set_jsgf_file(ps, GRS, GRAMMAR_PATH);

	/* Begin Recognition */
	recognize_from_microphone();



	return 0;
}
