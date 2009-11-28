/**
 * Starting point. Saves / loads properties.
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.io.IOException;

//import javax.swing.JOptionPane;
//import javax.swing.JFrame;
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
				//TODO: Need to delete any old properties that have been removed or replaced in an earlier version.
				_properties.saveProperties( propertiesFile );
			}
		}
		
		String launch = "";
		
		if( args == null || args.length == 0 || args[0].equals("") )
			launch = "window"; // default.
		else
			launch = args[0].toLowerCase();
		
		if( launch.equals( "window" ) )
		{
			int number = 1;
			
			if( args.length > 1 )
			{
				try {
					number = Integer.parseInt( args[1] ); }
				catch( Exception e ) {}
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
			
			// TODO: Next line causes a hang, program does not close properly.
			//JOptionPane.showMessageDialog(null, "Sorry, no settings yet. You can make some changes in" +
			//		" photoss.properties file at USER/akademy.co.uk/photoss/");
		}
		else if( launch.equals( "help" ) )
		{
			OutputInformation();
			OutputHelp();
		}
		else
		{
			OutputInformation();
			System.err.println( "Error: Your command was not found.");
			OutputHelp();
		}
		
		// Save properties.
		// TODO: this should be left till the program closes, it falls here much earlier than that though
		_properties.saveProperties( propertiesFile );
	}
	
	private static void OutputInformation()
	{
		System.out.println("PhotoSS : Photos everywhere. Copyright, akademy.co.uk 2009.");
	}
	
	private static void OutputHelp()
	{
		System.out.println("To show in full screen add \"fullscreen\".");
		System.out.println("To show in a one or more windows add \"window <number>\".");
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
