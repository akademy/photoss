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
	static String PROPERTY_FILE = "PhotosFromFlickr.properties";
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
		catch (IOException e1)
		{
			// Set defaults then
			_properties.setProperty("flickr.apiSecret","<NEED_SECRET>" ); // Warning: Do not submit to subversion!!!
			_properties.setProperty("flickr.apiKey","<NEED_APIKEY>"); // Warning: Do not submit to subversion!!!
			_properties.setProperty("flickr.userToken","<NEED_USERTOKEN>");
			_properties.setProperty("flickr.photoCount","25" );
			_properties.setProperty("flickr.daysToReconnect","7");
			_properties.setProperty("flickr.lastConnection","");
			_properties.setProperty("general.proxyHost","");
			_properties.setProperty("general.proxyPort","8080");
			
			_properties.saveProperties( propertiesFile );
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
		_properties.saveProperties( propertiesFile );
	}
}
