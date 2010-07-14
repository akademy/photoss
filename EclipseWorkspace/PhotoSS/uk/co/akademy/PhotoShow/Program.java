/**
 * Starting point. Saves / loads properties.
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
			if( propertyDefaults() )
			{
				//TODO: Need to delete any old properties that have been removed or replaced in an earlier version.
				_properties.saveProperties( propertiesFile );
			}
		}
		
		String launch = "";
		
		if( args == null || args.length == 0 || args[0].equals("") )
		{
			System.out.println("No choice made, using \"window 1\".");
			outputHelp();
			
			launch = "window"; // default.
		}
		else
			launch = args[0].toLowerCase();
		
		if( launch.equals( "window" ) )
		{
			int number = 1;
			
			if( args.length > 1 )
			{
				try {
					number = Integer.parseInt( args[1] ); }
				catch( Exception e ) {
					System.err.println( "Error: You need to pass in a number: photoss window <number>.");
				}
			}
			
			new WindowShow( number );
		}
		else if( launch.equals( "screensaver" ) || launch.equals( "fullscreen" ) )
		{
			boolean showScreensaver = true;
			
			if( args.length > 1 )
			{
				if( args[1].equals( "preview" ) )
				{
					// handle preview
					//String previewWindow = args[2];
					showScreensaver = false;
				}
			}
			
			if( showScreensaver )
				new FullScreenShow();
		}
		else if( launch.equals( "settings" ) )
		{
			// Open settings dialog
			// - We may need different settings for each way to launch it...
			if( args.length > 1 )
			{
				if( args[1].equals( "screensaver" ) )
				{
					// default to screensaver settings
				}
			}
			
			new PropertyChanger( _properties );
		}
		else if( launch.equals( "help" ) )
		{
			outputInformation();
			outputHelp();
		}
		else
		{
			outputInformation();
			System.err.println( "Error: Your command was not found.");
			outputHelp();
		}
		
		// Save properties.
		// TODO: this should be left till the program closes, it falls here much earlier than that though
		_properties.saveProperties( propertiesFile );
	}
	
	private static void outputInformation()
	{
		System.out.println("PhotoSS : Photos everywhere. Copyright, akademy.co.uk 2010.");
	}
	
	private static void outputHelp()
	{
		System.out.println("Command line settings:");
		System.out.println("photoss fullscreen - To show in full screen.");
		System.out.println("photoss window <number> - To show in one or more windows.");
	}
	
	private static boolean propertyDefaults()
	{
		boolean changesMade = false;
		
		if( setPropertyIfNone("flickr.apiSecret","" ) ) // Warning: Do not submit apiSecret to subversion!!!
			changesMade = true;
		
		if( setPropertyIfNone("flickr.apiKey","") ) // Warning: Do not submit apikey to subversion!!!
			changesMade = true;

		if( setPropertyIfNone( "flickr.userToken","") )
			changesMade = true;

		if( setPropertyIfNone( "flickr.photoCount","25") )
			changesMade = true;

		if( setPropertyIfNone( "flickr.daysToReconnect","7") )
			changesMade = true;

		if( setPropertyIfNone( "flickr.lastConnection","") )
			changesMade = true;
		
		if( setPropertyIfNone( "flickr.photosets",""))
			changesMade = true;

		// TODO Add proxyUse so we can turn it off and on quickly.
		
		if( setPropertyIfNone( "general.proxyHost","") )
			changesMade = true;

		if( setPropertyIfNone( "general.proxyPort","8080") )
			changesMade = true;

		if( setPropertyIfNone( "general.photoShowTime", "10000") )
			changesMade = true;

		if( setPropertyIfNone( "folder.folders", "") )
			changesMade = true;
		
		return changesMade;
	}
	
	private static boolean setPropertyIfNone( String property, String value )
	{
		String currentValue = Program.getProperty( property );
		if( currentValue == null )
		{
			// Nothing set, so use default
			Program.setProperty( property , value );
			return true;
		}
		
		return false;
	}
}

// http://www.dis.uniroma1.it/~liberato/screensaver/