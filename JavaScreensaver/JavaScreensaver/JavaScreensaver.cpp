

#include "stdafx.h"

#include <windows.h>
#include <iostream>
#include <conio.h> // _getch();

void _tmain( int argc, TCHAR *argv[] )
{
	// Display paremters passwed to screen saver
	/*TCHAR str[200];

	wcscpy_s( str, 200, L"Screensaver parameters:\n" );
	for( int i = 0; i<argc; i++ )
	{
		wcscat_s( str, 200, L" " );
		wcscat_s( str, 200, L"\"" );
		wcscat_s( str, 200, argv[i] );
		wcscat_s( str, 200, L"\"" );
	}
	
	wprintf( L"%s", str );

	*/

	if( _wcsicmp( argv[1], L"/p" ) )
	{
		TCHAR str[600];
		int i = 0;

		//wcscpy_s( str, 200, L"-jar \"./photoss.jar\"" );
		wcscpy_s( str, 200, L"\"C:\\Program Files\\Java\\jre1.6.0_07\\bin\\java.exe\" -jar " );
		wcscat_s( str, 200, L"\"" );
		wcscat_s( str, 200, argv[0] );

		int iLength = wcslen( str );
		for( i = iLength; str[i] != '\\' && i > 0; i-- )
			;

		if( i != 0 )
		{
			wcscpy_s( str + i, 200 - i, L"\\photoss.jar\"" );

			for( i = 1; i<argc; i++ )
			{
				wcscat_s( str, 200, L" " );
				wcscat_s( str, 200, L"\"" );
				wcscat_s( str, 200, argv[i] );
				wcscat_s( str, 200, L"\"" );
			}

			HANDLE hProcess = NULL;
			PROCESS_INFORMATION processInfo;
			STARTUPINFO startupInfo;
			::ZeroMemory(&startupInfo, sizeof(startupInfo));
			startupInfo.cb = sizeof(startupInfo);

			if(::CreateProcess( NULL, (LPTSTR)str, 
							   NULL,  // process security
							   NULL,  // thread security
							   FALSE, // no inheritance
							   0,     // no startup flags
							   NULL,  // no special environment
							   NULL,//L"C:\\Windows\\systems32",  // default startup directory
							   &startupInfo,
							   &processInfo))
			{ /* success */
				hProcess = processInfo.hProcess;

				::WaitForSingleObject(hProcess, INFINITE);
				::CloseHandle(hProcess);

			} /* success */
			else
			{
				int iError = GetLastError();
				if( iError != 0 )
				{
					iError = iError + 1;
				}
			}
			//ShellExecute(NULL, L"open", L"java.exe", str, NULL, SW_SHOWNORMAL );
		}

		//_getch();
	}



}
