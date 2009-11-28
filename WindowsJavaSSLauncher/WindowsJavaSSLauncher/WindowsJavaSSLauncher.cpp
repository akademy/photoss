// WindowsJavaSSLauncher.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "WindowsJavaSSLauncher.h"

#define MAX_COMMANDLINE_LENGTH 500

TCHAR ** GetCommandLineArray( LPTSTR lpCmdLine, int *pCommandsCount );
void FreeCommandLineArray( TCHAR **array );

int APIENTRY _tWinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPTSTR    lpCmdLine,
                     int       nCmdShow)

{
	// Obtain the command line paramters as an array.
	int argc = 0;
	TCHAR **argv = GetCommandLineArray( GetCommandLine(), &argc );

	TCHAR launchString[MAX_COMMANDLINE_LENGTH];

	_tcscpy_s( launchString, MAX_COMMANDLINE_LENGTH, _T("javaw -jar \"") ); // "javaw" doesn't flash up a command line, "java" does 
	_tcscat_s( launchString, MAX_COMMANDLINE_LENGTH, argv[0] ); // Path and filename of this app

	// Get the folder we are launching from and add the screen saver file
	int length = _tcslen( launchString );
	int filePathStart = length - _tcslen( argv[0] );
	int fileNameOffset = length - 1;
	while( _tcsncmp( launchString + fileNameOffset, _T("\\"), 1 ) && fileNameOffset > filePathStart )
		fileNameOffset--;

	if( fileNameOffset > filePathStart )
	{
		// Copy name over the top of the existing one. (Harded coded so it can only launch the specified file)
		_tcscpy_s( launchString + fileNameOffset, MAX_COMMANDLINE_LENGTH - fileNameOffset, _T("\\photoss.jar\"\0") );

		int command = 1;
		for( ; command < argc; command++ )
		{
			_tcscat_s( launchString, 200, _T(" \"") );

			// Windows screensaver info: // http://support.microsoft.com/kb/182383
			if( !_tcsncmp( argv[command], _T("/s"), 2 ) )
				_tcscat_s( launchString, 200, _T("screensaver") );
			else if( !_tcsncmp( argv[command], _T("/p"), 2 ) )
				_tcscat_s( launchString, 200, _T("screensaver\" \"preview") );
			else if( !_tcsncmp( argv[command], _T("/c"), 2 ) )
				_tcscat_s( launchString, 200, _T("settings\" \"screensaver") );
			else
				_tcscat_s( launchString, 200, argv[command] );

			_tcscat_s( launchString, 200, _T("\"") );
		}

		// Command lines no longer needed.
		FreeCommandLineArray( argv );

		PROCESS_INFORMATION processInfo;
		STARTUPINFO startupInfo;

		ZeroMemory(&startupInfo, sizeof(startupInfo));
		startupInfo.cb = sizeof(startupInfo);

		if(::CreateProcess( NULL, (LPTSTR)launchString, 
						   NULL,  // process security
						   NULL,  // thread security
						   FALSE, // no inheritance
						   0,     // no startup flags
						   NULL,  // no special environment
						   NULL,//L"C:\\Windows\\systems32",  // default startup directory
						   &startupInfo,
						   &processInfo))
		{ // success
			HANDLE hProcess = processInfo.hProcess;

			// A screensaver needs to stay open, therefore sos does this app.
			WaitForSingleObject(hProcess, INFINITE);
			CloseHandle(hProcess);
		}
		else
		{
			int iError = GetLastError();
		}
	}
}

// Obtain the command line parameters as an array of strings
TCHAR ** GetCommandLineArray( LPTSTR lpCmdLine, int *pCommandsCount )
{
   int 
        commandsMaximum = 10,
        commandsCount = 0;
   
   // allocate space for 10 commands (pointers to the command strings), plus a null one. Expand later if needed.
   TCHAR** commands = (TCHAR**)calloc( commandsMaximum + 1, sizeof(TCHAR*) ); 

   TCHAR *commandLine = lpCmdLine;
   TCHAR *last = commandLine + _tcslen( commandLine );

   while( _tcsncmp( commandLine, _T("\0"), 1 ) )
   {
       // Skip leading spaces
       while( !_tcsncmp( commandLine, _T(" "), 1 ) )
            commandLine++;

       TCHAR 
            *start = commandLine,
            *end = NULL,
            *quote = _tcsstr( commandLine, _T( "\"" ) ),
            *space = _tcsstr( commandLine, _T( " " ) );

        if( space == NULL )
        {
            // No spaces left, just the end of the command list.
            end = last;
        }
        else
        {
            if( quote != NULL && quote < space )
            {
                //The space may be inside the quotes so skip to end of quote string.
                quote = _tcsstr( quote+1, _T( "\"" ) );
                // Need to check this isn't an escaped quotaion mark
                while( quote && !_tcsncmp( quote-1, _T("\\"), 1 ) )
                    quote = _tcsstr( quote+1, _T( "\"" ) );

                if( quote )
                    space = _tcsstr( quote, _T( " " ) );
                else // Unmatched quotes.
                    space = NULL;
            }
            
            end = ( space != NULL ) ? space : last;
        }

        TCHAR 
            *commandStart = start,
            *commandEnd = end;

        if( !_tcsncmp( commandStart, _T("\""), 1 ) )
            commandStart++;

        while( commandEnd > commandStart && !_tcsncmp( commandEnd-1, _T(" "), 1 ) )
            commandEnd--;
        if( !_tcsncmp( commandEnd-1, _T("\""), 1 ) )
            commandEnd--;

        if( commandStart != commandEnd )
        {
            if( commandsMaximum == commandsCount + 1 )
            {
                // more space for commands needed 
                commandsMaximum += 10;
                TCHAR** commandsNew = (TCHAR**)calloc( commandsMaximum + 1, sizeof(TCHAR*) );
                memcpy( commandsNew, commands, commandsCount * sizeof( TCHAR*) );

                free( commands );
                commands = commandsNew;
            }

            commands[commandsCount] = (TCHAR*)calloc( commandEnd - commandStart + 1, sizeof( TCHAR ) );
            _tcsncpy_s( commands[commandsCount], commandEnd - commandStart + 1, commandStart, commandEnd - commandStart );

            commandsCount++;
        }

        if( end == last )
            commandLine = end;
        else
            commandLine = end + 1;
   }

   if( commandsCount < commandsMaximum )
   {
        // remove the unused command string spaces, not particualrly necessary but neater, saves a bit of memory.
        TCHAR** commandsNew = (TCHAR**)calloc( commandsCount + 1, sizeof(TCHAR*) );
        memcpy( commandsNew, commands, commandsCount * sizeof( TCHAR*) );

        free( commands );
        commands = commandsNew;
   }

    *pCommandsCount = commandsCount;
    return commands;
}

// Free command line array
void FreeCommandLineArray( TCHAR **array )
{
    TCHAR **command = array;
    while( *command )
        free( *(command++) );

    free( array );
}