#include "speechutils.h"

/* Setup Functions */
void speech_utils_init()
{
	tts = register_cmu_us_kal("slt");
	verbose = 0;
	current_name = "trialnetwork";
}

void speak(const char* s)
{
	flite_text_to_speech(s, tts, "play");
}

/* Initial Responses */
void initial_response()
{
	char *s = "Yes?";
	speak(s);
	
	start_interaction();
}

void help_menu_initial()
{
	char *s;
	if(verbose)
		s = "Please say internet, bluetooth, or system to interact with their respective settings. Say cancel to stop the interaction at any time.";
	else
		s = "Internet, bluetooth, system.";
	speak(s);
	
	start_interaction();
}


void cancel()
{
	char *s = "Action cancelled";
	speak(s);
	
	start_interaction();
}

/* Help Menus for Internet */
void hmi_start()
{
	char *s;
	if(verbose)
		s = "Please say status to hear your internet status, or connect to connect to a new network.";
	else
		s = "Status, connect";
	speak(s);
	
	start_interaction();
}

void hmi_is_name_known()
{
	char *s;
	if(verbose)
		s = "Do you know the name of the network that you want to connect to? Please say yes or no.";
	else
		s = "Yes, no.";
	speak(s);

	start_interaction();
}

void hmi_unknown_name()
{
	char *s;
	if(verbose)
	{	
		s = "Please say connect to connect to ";
		strcat(s, current_name);
		strcat(s, " say skip to move to the next network");	
	}
	else
	{
		s = current_name;
		strcat(s, ", skip, connect.");
	}
	speak(s);

	start_interaction();
}

void hmi_password()
{
	char *s = "Precede capital letters with the word capital. Precede numbers with the word number. Precede special characters with the word special. Say back to delete the last character spoken. Say clear to delete all characters spoken. Say cancel to stop connecting to this network.";
	speak(s);
	
	start_interaction();
}

/* Replies for Internet */
void ri_status(){}
void ri_scan_connections(){}
void ri_unknown_name(){}
void ri_unknown_name_helper(){}
void ri_known_name(){}
void ri_known_confirmation(){}
void ri_known_confirmation_cld(){}
void ri_public_connecting(){}
void ri_password(){}
void ri_password_character(){}
void ri_password_get_char(){}
void ri_password_get_punctuation(){}
void ri_password_connecting(){}

/* Help Menus for System */
void hms_start(){}
void hms_verbosity(){}
void hms_restart(){}

/* Replies for System */
void rs_status(){}
void rs_verbosity(){}
void rs_verbosity_confirmation(){}
void rs_reset(){}
void rs_reset_confirmation(){}

#endif _SPEECH_UTILS_H_
