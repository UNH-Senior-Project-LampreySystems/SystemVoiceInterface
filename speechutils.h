#ifndef _SPEECH_UTILS_H_
#define _SPEECH_UTILS_H_

#include "flite.h"
#include "string.h"


/* A global speech recognition variable */
cst_voice *tts;
int verbose;

/* Setup Functions */
cst_voice* register_cmu_us_kal(const char*);
void speech_utils_init();
void speak(const char*);

/* Initial Responses */
void initial_response();
void help_menu_initial();
void cancel();

/* Help Menus for internet */
void hmi_start();
void hmi_is_known();
void hmi_unknown();
void hmi_known();
void hmi_confirmation();
void hmi_password();

/* Helper functions for internet */
void hfi_scan_connections();
void hfi_unknown();
void hfi_unknown_helper();
void hfi_known_comparison();
unsigned int hfi_compare(char *s1, char *s2);
void hfi_password(char *c);

/* Replies for internet */
void ri_status();
void ri_connect();
void ri_connect_password(char *password);

/* Help Menus for System */
void hms_start();
void hms_verbosity();
void hms_restart();

/* Replies for System */
void rs_status();
void rs_verbosity(int bool);
void rs_reset(int bool);

#endif
