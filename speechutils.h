#ifndef _SPEECH_UTILS_H_
#define _SPEECH_UTILS_H_

#include "flite.h"
#include "string.h"


/* A global speech recognition variable */
cst_voice *tts;
int verbose;
char *current_name;

/* Setup Functions */
cst_voice* register_cmu_us_kal(const char*);
void speech_utils_init();
void speak(const char*);

/* Initial Responses */
void initial_response();
void help_menu_initial();
void cancel();

/* Help Menus for voidernet */
void hmi_start();
void hmi_is_name_known();
void hmi_unknown_name();
void hmi_password();

/* Replies for voidernet */
void ri_status();
void ri_scan_connections();
void ri_unknown_name();
void ri_unknown_name_helper();
void ri_known_name();
void ri_known_confirmation();
void ri_known_confirmation_cld();
void ri_public_connecting();
void ri_password();
void ri_password_character();
void ri_password_get_char();
void ri_password_get_punctuation();
void ri_password_connecting();

/* Help Menus for System */
void hms_start();
void hms_verbosity();
void hms_restart();

/* Replies for System */
void rs_status();
void rs_verbosity(int bool);
void rs_reset(int bool);

#endif
