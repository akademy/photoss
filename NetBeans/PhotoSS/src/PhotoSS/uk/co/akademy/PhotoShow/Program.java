/**
 * Starting point. Saves / loads properties.
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author matthew
 *
 */
public class Program
{
	static public String VERSION = "0.4.1.53";

	static String PROPERTY_FILE = "PhotoSS.properties";
	static String _propertiesFile = null;
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

	public static void loadProperties()
	{
		try
		{
			_properties.loadProperties( _propertiesFile );
		}
		catch (IOException e1) {}
		finally
		{
			if( propertyDefaults() )
			{
				//TODO: Need to delete any old properties that have been removed or replaced in an earlier version.
				_properties.saveProperties( _propertiesFile );
			}
		}
	}
	
	public static void saveProperties()
	{
		_properties.saveProperties( _propertiesFile );
	}
	
	/**
	 * @param args
	 */
	public static void main( String[] args )
	{		
		outputInformation();

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
		_propertiesFile = programWorkingFolder + PROPERTY_FILE;
		
		loadProperties();
		
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
			int windowCount = 1;
			
			if( args != null && args.length > 1 )
			{
				try {
					int windowCountCmd = Integer.parseInt( args[1] ); 
					
					if( windowCountCmd <= 10 && windowCountCmd > 0) {
						windowCount = windowCountCmd;
					}
				}
				catch( NumberFormatException e ) {
					System.err.println( "Error: You need to pass in a number: photoss window <number>.");
				}
			}
			
			show( new Show_Window( windowCount ) );
		}
		else if( launch.equals( "fullscreen" ) )
		{
			int screenNumber = 1;
			
			if( args != null && args.length > 1 )
			{
				try {
					screenNumber = Integer.parseInt( args[1] ); }
				catch( NumberFormatException e ) {
					System.err.println( "Error: You need to pass in a number: photoss fullscreen <number>.");
				}
			}
			
			show( new Show_FullScreen( screenNumber ) );
		}
		else if( launch.equals( "screensaver" ) )
		{
			boolean showScreensaver = true;
			
			if( args != null && args.length > 1 )
			{
				if( args[1].equals( "preview" ) )
				{
					//TODO: Handle screensaver preview. (Almost certainly platform specific though)
					//String previewWindow = args[2];
					showScreensaver = false;
				}
			}
			
			if( showScreensaver )
			{
				show( new Show_Screensaver() );
			}
		}
		else if( launch.equals( "settings" ) )
		{
			// Open settings dialog
			// - We may need different settings for each way to launch it...
			if( args != null && args.length > 1 )
			{
				if( args[1].equals( "screensaver" ) )
				{
					// default to screensaver settings
				}
			}
			
			PropertyChanger ignoreReturn = new PropertyChanger( _properties );
		}
		else if( launch.equals( "help" ) )
		{
			outputHelp();
		}
		else
		{
			System.err.println( "Error: Your command was not found.");
			outputHelp();
		}
		
		// Save properties.
		// TODO: saving of properties should be left till the program closes, it falls here much earlier than that though
		saveProperties();
	}
	
	private static void show( Show show )
	{
		if( show.initilise() )
		{
			ArrayList<AbstractPhotosFrom> photosFromList = new ArrayList<AbstractPhotosFrom>();
                        
			// Dynamic load (kind of)
			String[] photosFrom = {
				"uk.co.akademy.PhotoShow.PhotosFrom_Folder",
				"uk.co.akademy.PhotoShow.PhotosFrom_Flickr",
				//"uk.co.akademy.PhotosFrom.Webpage.PhotosFrom_Webpage",
				//"uk.co.akademy.PhotosFrom.Example.PhotosFrom_Example"
				//"uk.co.akademy.PhotoShow.PhotosFrom_Test"
			};


			ClassLoader classLoader = Program.class.getClassLoader();

			try {
				for (String photosFrom1 : photosFrom) {
					Class photoFrom = classLoader.loadClass(photosFrom1);
					photosFromList.add( (AbstractPhotosFrom)photoFrom.newInstance() );
				}
			}
			catch ( IllegalAccessException e) {
			}
			catch ( InstantiationException e) {
			}
			catch (ClassNotFoundException e) {
			}
                        
			ShowControl sc = new ShowControl( show, photosFromList );

			if( !sc.start() ) {
				System.out.println("Error: Nothing to show");
			}
			
		}
	}
	
	private static void outputInformation()
	{
		System.out.println("PhotoSS : Photos everywhere. Version " + VERSION + ". Copyright, akademy.co.uk 2014.");
	}
	
	private static void outputHelp()
	{
		System.out.println("Options:");
		System.out.println("photoss settings - To edit the settings in a window.");
		System.out.println("photoss fullscreen - To show in full screen.");
		System.out.println("photoss screensaver - To show in full screen close with mouse or key.");
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

		// TODO Add proxyUse so we can turn it on and off without removing other properties.
		
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