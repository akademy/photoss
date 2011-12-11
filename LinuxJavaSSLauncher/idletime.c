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

#define SLEEP_TIME (1 * SECOND)
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

int main( int argCounts, char *argValues[] ) {

	if( argCounts == 2 || argCounts == 3 )
	{
		long lMinutes = 0;
		char *sProgram = NULL;
		
		if( argCounts == 2 )
		{
			lMinutes = IDLE_TIME_DEFAULT_LIMIT / 60;
			sProgram = argValues[1];
		}
		else
		{
			lMinutes = strtol( argValues[1], NULL, 10 );
			sProgram = argValues[2];
		}
		
		for(;;) 
		{
			printf( "%s, %ld: %d\n", sProgram, lMinutes, GetIdleTime() );

			sleep(SLEEP_TIME);
		}
	}
	else
	{
		printf( "%s\n", "Need <Idletime in minutes (optional, 5 minute default)> <application position>" );
	}
}
