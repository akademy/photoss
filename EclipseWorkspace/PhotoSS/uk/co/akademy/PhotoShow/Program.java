/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.io.IOException;

/**
 * @author matthew
 *
 */
public class Program
{
	static String PROPERTY_FILE = "PhotoSS.properties";
	static PropertyFetcher _properties = null;
	
	static public String getFolder()
	{
		String programWorkingFolder = System.getProperty("user.home") + File.separator;
		programWorkingFolder += ".akademy.co.uk" + File.separator ;
		programWorkingFolder += "photoss" + File.separator ;
		
		return programWorkingFolder;
	}
	
	static public String getProperty( String property )
	{
		return _properties.getProperty( property );
	}
	
	static public void setProperty( String property, String value )
	{
		_properties.setProperty( property, value );
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

		//
		// Load properties.
		//
		_properties = new PropertyFetcher();
		String propertiesFile = programWorkingFolder + PROPERTY_FILE;
		
		try
		{
			_properties.loadProperties( propertiesFile );
		}
		catch (IOException e1) {}
		finally
		{
			if( PropertyDefaults() )
			{
				//TODO: Need to delete any old properties that have been removed or replaced.
				_properties.saveProperties( propertiesFile );
			}
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
		
		// Save properties.
		// TODO: this should be left till the program closes, it falls here much earlier than that though
		_properties.saveProperties( propertiesFile );
	}
	
	private static boolean PropertyDefaults()
	{
		boolean changesMade = false;
		
		if( SetPropertyDefaultIfNone("flickr.apiSecret","<NEED_SECRET>" ) ) // Warning: Do not submit secret to subversion!!!
			changesMade = true;
		
		if( SetPropertyDefaultIfNone("flickr.apiKey","<NEED_APIKEY>") ) // Warning: Do not submit apikey to subversion!!!
			changesMade = true;

		if( SetPropertyDefaultIfNone( "flickr.userToken","<NEED_USERTOKEN>") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "flickr.photoCount","25") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "flickr.daysToReconnect","7") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "flickr.lastConnection","") )
			changesMade = true;

		// TODO Add proxyUse so we can turn it off and on quickly.
		
		if( SetPropertyDefaultIfNone( "general.proxyHost","") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "general.proxyPort","8080") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "general.photoShowTime", "10000") )
			changesMade = true;

		if( SetPropertyDefaultIfNone( "folder.folders", "<NEED_PATHS_TO_PHOTOS>") )
			changesMade = true;
		
		return changesMade;
	}
	
	private static boolean SetPropertyDefaultIfNone( String property, String value )
	{
		String currentValue = Program.getProperty( property );
		if( currentValue == null )
		{
			Program.setProperty( property , value );
			return true;
		}
		
		return false;
	}
}
