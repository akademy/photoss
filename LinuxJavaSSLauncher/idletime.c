// http://stackoverflow.com/questions/222606/detecting-keyboard-mouse-activity-in-linux
// apt-get install libx11-dev libxss-dev
// gcc -o idletime -L/usr/X11R6/lib/ -lX11 -lXext -lXss idletime.c
//
#include <time.h>
#include <stdio.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/extensions/scrnsaver.h>

#define SECOND 1
#define MINUTE (60 * SECOND)

#define SLEEP_TIME (10 * SECOND)
#define IDLE_TIME_DEFAULT_LIMIT (5 * MINUTE)

int GetIdleTime () {
	time_t idle_time;
	static XScreenSaverInfo *mit_info;
	Display *display;
	int screen;
	mit_info = XScreenSaverAllocInfo();
	if((display=XOpenDisplay(NULL)) == NULL) { return(-1); }
	screen = DefaultScreen(display);
	XScreenSaverQueryInfo(display, RootWindow(display,screen), mit_info);
	idle_time = (mit_info->idle) / 1000;
	XFree(mit_info);
	XCloseDisplay(display); 
	return idle_time;
}

int main( int argCounts, char *argValues[] )
{
	int debug = 0;
		
	if( argCounts == 2 || argCounts == 3 )
	{
		int iSecondsOfIdle = 0;
		char *sProgramToLaunch = NULL;
		
		if( argCounts == 2 )
		{
			iSecondsOfIdle = IDLE_TIME_DEFAULT_LIMIT;
			sProgramToLaunch = argValues[1];
		}
		else
		{
			iSecondsOfIdle = strtol( argValues[1], NULL, 10 ) * 60;
			sProgramToLaunch = argValues[2];
		}
		
		for(;;) 
		{
			int iIdleTime = GetIdleTime();
			
			if( debug )
				printf( "%s, %d: %d\n", sProgramToLaunch, iSecondsOfIdle, iIdleTime );
			
			if( iIdleTime >= iSecondsOfIdle )
			{
				if( debug )
					printf( "Launching:%s\n", sProgramToLaunch );
				
				system( sProgramToLaunch );
			}
			

			sleep(SLEEP_TIME);
		}
	}
	else
	{
		printf( "%s\n", "Need <Idletime in minutes (optional, 5 minute default)> <command to run>" );
	}
}
