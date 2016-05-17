#ifndef _MAIN_ACTIVITY_H_
#define _MAIN_ACTIVITY_H_

#include <pocketsphinx.h>
#include <ps_search.h>
#include <string.h>
#include <assert.h>
#include <sys/select.h>
#include <sphinxbase/err.h>
#include <sphinxbase/ad.h>
#include <sphinxbase/fsg_model.h>
#include <sphinxbase/ngram_model.h>
#include "speechutils.h"


static const char KWS[] = "key word search";
static const char KEY_PHRASE[] = "wake";
static const char GRS[] = "grammar search";
static const char GRAMMAR_PATH[] = "menu.gram";

enum nodes
{
START,
INT_START,
INT_CONNECT,
INT_UNKNOWN,
INT_KNOWN,
INT_CONF,
INT_PASS,
SYS_START,
SYS_VERBOSITY,
SYS_RESET
};

void sleep_msec(int32 ms);
void recognize_from_microphone();
void start_interaction();
void reset_interaction();

void parse_to_depth(char * tokens);
void parse_to_start(char * token);

void parse_to_internet_start(char * token);
void parse_to_internet_is_known(char * token); 
void parse_to_internet_unknown(char * token);


void parse_to_system_start(char * token);
void parse_to_system_verbosity(char * token);
void parse_to_system_reset(char * token);

#endif
