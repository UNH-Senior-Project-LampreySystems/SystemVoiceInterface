#include "main-activity.h"

ps_decoder_t *ps = NULL;
cmd_ln_t *config = NULL;

enum nodes node = START;

void sleep_msec(int32 ms)
{
	struct timeval tmo;
	tmo.tv_sec = 0;
	tmo.tv_usec = ms * 1000;
	select(0, NULL, NULL, NULL, &tmo);
}

void start_interaction()
{
	ad_rec_t *ad;
	int16 adbuf[2048];
	uint8 utt_started, in_speech;
	int32 k;
	char const *hyp;

	/* Set up the microphone */
	if((ad = ad_open_dev(cmd_ln_str_r(config, "-adcdev"),
			(int) cmd_ln_float32_r(config, "-samprate"
							))) == NULL)
		E_FATAL("Failed to open audio device\n");
	if(ad_start_rec(ad) < 0)
		E_FATAL("Failed to start recording\n");
	
	if(ps_start_utt(ps) < 0)
		E_FATAL("Failed to start utterance\n");

	/*Start the recognition */
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
				{
					ps_set_search(ps, GRS);
				}
			
				if(strcmp(ps_get_search(ps), GRS) == 0)
				{
					ad_close(ad);
					if(strcmp(hyp, "cancel") == 0)
					{
						cancel();
						reset_interaction();
						return;
					}
					else
					{
						char *tokens = (char*) hyp; 
						parse_to_depth(tokens);
						return;
					}
				}


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

void reset_interaction()
{
	node = START;
	ps_set_search(ps, KWS);
	start_interaction();
}

/****************************************
 * parse speech input		
 ****************************************/
void parse_to_depth(char * tokens)
{
	switch(node)
	{
		case START:
			parse_to_start(tokens);
			break;
		case INT_START:
			parse_to_internet_start(tokens);
			break;
		case SYS_START:
			parse_to_system_start(tokens);
			break;
		case SYS_VERBOSITY:
			parse_to_system_verbosity(tokens);
			break;
		case SYS_RESET:
			parse_to_system_reset(tokens);
			break;
	}
}

void parse_to_start(char * token)
{
	if(strcmp(token, "internet") == 0)
	{
		node = INT_START;
		hmi_start();	
		return;
	}
	else if(strcmp(token, "system") == 0)
	{
		node = SYS_START;
		hms_start();
		return;
	}
	
	if(node == START)
		help_menu_initial();
}

/****************************************
 * parse speech input for internet		
 ****************************************/
void parse_to_internet_start(char * token)
{
	if(strcmp(token, "status") == 0)
	{
		node = START;
		ri_status();
	}
	else if(strcmp(token, "connect") == 0)
	{
		node = INT_CONNECT;
		hfi_scan_connections();
	}

	if(node == INT_START)
		hmi_start();
}



/****************************************
 * parse speech input for system		
 ****************************************/
void parse_to_system_start(char * token)
{
	if(strcmp(token, "status") == 0)
	{
		node = START;
		rs_status();	
		return;
	}
	else if(strcmp(token, "verbosity") == 0)
	{
		node = SYS_VERBOSITY;
		hms_verbosity();
		return;
	}
	else if(strcmp(token, "reset") == 0)
	{
		node = SYS_RESET;
		hms_restart();
	}


	if(node == SYS_START)
		hms_start();
}

void parse_to_system_verbosity(char * token)
{
	if(strcmp(token, "verbose") == 0)
	{
		node = START;
		rs_verbosity(1);	
		return;
	}
	else if(strcmp(token, "quiet") == 0)
	{
		node = START;
		rs_verbosity(0);
		return;
	}

	if(node == SYS_VERBOSITY)
		hms_verbosity();
}

void parse_to_system_reset(char * token)
{
	if(strcmp(token, "yes") == 0)
	{
		node = START;
		rs_reset(1);	
		return;
	}
	else if(strcmp(token, "no") == 0)
	{
		node = START;
		rs_reset(0);
		return;
	}

	if(node == SYS_VERBOSITY)
		hms_verbosity();
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

	/* Setup the text to speech */
	speech_utils_init();

	/* Begin Recognition */
	start_interaction();

	return 0;
}
