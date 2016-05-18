#include "speechutils.h"
#include "main-activity.h"
#include "systemutils.h"
#include "internetutils.h"

#define MIN3(a, b, c) ((a) < (b) ? ((a) < (c) ? (a) : (c)) : ((b) < (c) ? (b) : (c)))

struct connection_list* network_list;
struct connection_list* current_network;


/* Setup Functions */
void speech_utils_init()
{
	tts = register_cmu_us_kal("slt");
	verbose = 1;
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
		s = "Please say internet, or system to interact with their respective settings. Say cancel to stop the interaction at any time.";
	else
		s = "Internet, system.";
	speak(s);
	
	start_interaction();
}


void cancel()
{
	char *s = "Action cancelled";
	speak(s);
}

/************************
 * Help menus for Internet
 ************************/
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

void hmi_is_known()
{
	char *s;
	if(verbose)
		s = "Do you know the name of the network that you want to connect to? Please say yes or no.";
	else
		s = "Yes, no.";
	speak(s);

	start_interaction();
}

void hmi_unknown()
{
	char *s;
	if(verbose)
	{	
		s = "Please say connect to connect to ";
		strcat(s, current_network->c->name);
		strcat(s, " say skip to move to the next network");	
	}
	else
	{
		s = current_network->c->name;
		strcat(s, ", skip, connect.");
	}
	speak(s);

	start_interaction();
}

void hmi_known()
{
	char *s = "Please spell the network name character by character, and say done when finished.";
	speak(s);

	start_interaction();
}

void hmi_confirmation()
{
	char s[300];
	if(verbose)
	{
		strcpy(s, "Are you sure you want to connect to ");
		strcat(s, current_network->c->name);
		strcat(s, ". Please say yes or no");
	}
	else
	{
		strcpy(s, current_network->c->name);
		strcat(s, " . Yes, no.");
	}
	speak(s);

	start_interaction();
}

void hmi_connect()
{
	if(current_network->c->password)
	{
		char s[300];
		strcpy(s, "This network requires a password. Please say the first character of the password or help for more options");
		speak(s);
		start_interaction();
	}
	else
	{
		char s[300];
		if(get_internet_connect(current_network->c))
		{
			strcpy(s, "successfully connected to ");
			strcat(s, current_network->c->name);
		}
		else
		{
			strcpy(s, "could not connect to ");
			strcat(s, current_network->c->name);
		}
		speak(s);
		reset_interaction();
	}
}

void hmi_password()
{
	char *s = "Precede capital letters with the word capital. Precede numbers with the word number. Precede special characters with the word special. Say back to delete the last character spoken. Say clear to delete all characters spoken. Say cancel to stop connecting to this network.";
	speak(s);
	
	start_interaction();
}

void hmi_connect_password(char *password)
{
	char s[300];
	if(get_internet_connect_password(current_network->c, password))
	{
		strcpy(s, "successfully connected to ");
		strcat(s, current_network->c->name);
	}
	else
	{
		strcpy(s, "could not connect to ");
		strcat(s, current_network->c->name);
	}
	speak(s);
	reset_interaction();
}

/************************
 * Helper functions for Internet
 ************************/
void hfi_scan_connections()
{
	char *s = "scanning for internet connections, please wait";
	speak(s);
	get_internet_scan();

	if(network_list != NULL)
	{
		hmi_is_known();
	}
	else
	{
		char * s2 = "no connections found";
		speak(s);
		reset_interaction();
	}
}

void hfi_unknown()
{
	char *s = "I will now list all the networks 1 by 1, please say connect to connect to the network or skip to list the next network.";
	speak(s);	
	
	hfi_unknown_helper();
}

void hfi_unknown_helper()
{
	if(current_network != NULL && current_network->next != NULL)
		current_network = current_network->next;
	else
		current_network = network_list;

	char *s = current_network->c->name;
	speak(s);
	
	start_interaction();
}

void hfi_known_comparison(char *network)
{
	fprintf(stderr, "\n\n\n%s\n\n\n", network);


	struct connection_list *temp_net = network_list;
	unsigned int best = 999999;

	while(temp_net != NULL)
	{
		unsigned int temp = hfi_compare(network, temp_net->c->name);
		if(temp < best)
		{
			best = temp;
			current_network = temp_net;
			fprintf(stderr, "\n\n\n%s %u\n\n\n", current_network->c->name, best);

		}

		temp_net = temp_net->next;
	}

	fprintf(stderr, "\n\n\n%s %u\n\n\n", current_network->c->name, best);

	hmi_confirmation();
}

unsigned int hfi_compare(char *s1, char *s2)
{
	unsigned int x, y, s1len, s2len;
	s1len = strlen(s1);
	s2len = strlen(s2);

	unsigned int matrix[s2len+1][s1len+1];
	matrix[0][0] = 0;
	for(x = 1; x <= s2len; x++)
		matrix[x][0] = matrix[x-1][0] + 1;
	for(y = 1; y <= s1len; y++)
		matrix[0][y] = matrix[0][y-1] + 1;
	for(x = 1; x <= s2len; x++)
		for(y = 1; y <=s1len; y++)
			matrix[x][y] = MIN3(matrix[x-1][y] + 1, matrix[x][y-1] + 1, matrix[x-1][y-1] + (s1[y-1] == s2[x-1] ? 0 : 1));

	return(matrix[s2len][s1len]);
}

void hfi_password(char *c)
{
	char s[300];
	strcpy(s, "I heard ");
	strcat(s, c);
	strcat(s, ". Next character.");
	speak(s);
	
	start_interaction();
}

/************************
 * Replies for Internet
 ************************/
void ri_status()
{
	char *s;
	s = (char*) get_internet_status(); 
	speak(s);	

	reset_interaction();
}

void ri_public_connecting(){}
void ri_password(){}
void ri_password_character(){}
void ri_password_get_char(){}
void ri_password_get_punctuation(){}
void ri_password_connecting(){}

/************************
 * Help menuse for System
 ************************/
void hms_start()
{
	char *s;
	if(verbose)
	{
		s = "Please say status to hear your system status, verbosity to set my response verbosity, or reset to reset the application.";
	}
	else
	{
		s = "Status, verbosity, reset.";
	}
	speak(s);

	start_interaction();
}

void hms_verbosity()
{
	char *s;
	if(verbose)
	{
		s = "Replies are currently verbose, would you like them to remain verbose or switch to quiet. Please say quiet or verbose";	
	}
	else
	{
		s = "Currently quiet. Quiet or verbose?";
	}
	speak(s);

	start_interaction();
}

void hms_restart()
{
	char *s;
	if(verbose)
	{
		s = "Are you sure you want to reset the system? Please say yes or no.";
	}
	else
	{
		s = "Yes, no.";
	}
	speak(s);

	start_interaction();
}

/************************
 * Replies for System
 ************************/
void rs_status()
{
	char *s;
	s = (char*) get_system_status();
	speak(s);

	reset_interaction();
}

void rs_verbosity(int bool)
{
	char *s;
	if(bool)
	{
		s = "Replies will now be verbose"; 
	}
	else
	{
		s = "Replies will now be quiet";
	}
	speak(s);
	verbose = bool;

	reset_interaction();
}

void rs_reset(int bool)
{
	char *s;
	if(bool)
	{
		s = "The system will now reset";
	}
	else
	{
		s = "You have chosen to not reset the system";
	}
	speak(s);

	reset_system();	
	
	reset_interaction();	
}
