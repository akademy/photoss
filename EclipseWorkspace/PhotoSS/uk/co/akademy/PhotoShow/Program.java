/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.File;

/**
 * @author matthew
 *
 */
public class Program
{
	static public String getFolder()
	{
		String programWorkingFolder = System.getProperty("user.home") + File.separator;
		programWorkingFolder += ".akademy.co.uk" + File.separator ;
		programWorkingFolder += "photoss" + File.separator ;
		
		return programWorkingFolder;
	}
	
	/**
	 * @param args
	 */
	public static void main( String[] args )
	{		
		String programWorkingFolder = Program.getFolder();
		
		File programWorkingFolderFile = new File( programWorkingFolder );
		if( !programWorkingFolderFile.exists() )
		{
			programWorkingFolderFile.mkdirs();
		}

		// http://support.microsoft.com/kb/182383
		
		if( args == null || args.length == 0 || args[0].toLowerCase().startsWith("/c") ) // "/c:1234567"
		{
			// Show settings
		}
		else if( args[0].toLowerCase().equals("/p") ) // "/p" "1234567"
		{
			// Preview screen saver in a window
		}
		else if( args[0].toLowerCase().equals("/s") ) // "/s"
		{
			new FullScreenShow();
		}
	}
}
