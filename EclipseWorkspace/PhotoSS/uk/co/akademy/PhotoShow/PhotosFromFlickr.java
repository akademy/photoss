/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import uk.co.akademy.Downloader.Download;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.PhotoList;

/**
 * @author Matthew
 *
 */
public class PhotosFromFlickr extends PhotosFrom implements Observer
{
	static String PROPERTY_FILE = "PhotosFromFlickr.properties";
	static String DATA_FILE = "FlickrPhotos.data";
	static String DOWNLOADS_FOLDER = "PhotosFromFlickrDownloads" + File.separator;
	
	private ArrayList<Download> _downloads = null;

	/**
	 * 
	 */
	public PhotosFromFlickr()
	{
		super();
		
		_downloads = new ArrayList<Download>();
	}

	/* (non-Javadoc)
	 * @see uk.co.akademy.PhotoShow.PhotosFrom#initilise(uk.co.akademy.PhotoShow.PhotoCanvas)
	 */
	public boolean initilise()
	{
		String programWorkingFolder = Program.getFolder();
		
		PropertyFetcher props = new PropertyFetcher();
		String propertiesFile = programWorkingFolder + PROPERTY_FILE;
		
		try
		{
			props.loadProperties( propertiesFile );
		}
		catch (IOException e1)
		{
			// Set defaults then
			props.setProperty("apiSecret","<NEED_SECRET>" ); // Warning: Do not submit to subversion!!!
			props.setProperty("apiKey","<NEED_APIKEY>"); // Warning: Do not submit to subversion!!!
			props.setProperty("userToken","<NEED_USERTOKEN>");
			props.setProperty("photoCount","25" );
			props.setProperty("daysToReconnect","7");
			props.setProperty("lastConnection","");
			props.setProperty("myProxyHost","");
			props.setProperty("myProxyPort","8080");
			
			props.saveProperties( propertiesFile );
		}
		
		PhotoList pl = null;
		File dataFile = new File(programWorkingFolder + DATA_FILE);
		
		if( dataFile.exists() )
		{
			// Read the saved photolist if it exists.
			FileInputStream f_in = null;
			
			try
			{
				f_in = new FileInputStream( dataFile );
			} catch (FileNotFoundException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
	
			// Read object using ObjectInputStream
			ObjectInputStream obj_in = null;
			try
			{
				obj_in = new ObjectInputStream (f_in);
			} catch (IOException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			Object obj = null;
			try
			{
				obj = obj_in.readObject();
			} catch (IOException e3)
			{
				e3.printStackTrace();
			} catch (ClassNotFoundException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
	
			if (obj instanceof PhotoList)
			{
				pl = (PhotoList) obj;
			}
			
			try
			{
				if( f_in != null)
					f_in.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		boolean contactFlickr = false;
		
		if( pl == null )
		{
			// No saved photo list found
			contactFlickr = true;
		}
		
		if( !contactFlickr )
		{
			// Check if we need to update the list.
			String lastConnectionProp = props.getProperty("lastConnection");
			
			if( lastConnectionProp == "" )
			{
				// No date of last connections
				contactFlickr = true;
			}
			else
			{
				TimeZone tz = null;//TimeZone.getTimeZone( "UTC" ); // This causes the debugger to wrongly stop, so lets use the default one for now...
				tz = TimeZone.getDefault();
				
				DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
				Date lastConnectionDate;
				
				try
				{
					lastConnectionDate = dfm.parse(lastConnectionProp);	
					
					String daysToReconnectProp = props.getProperty("daysToReconnect");
					int daysToReconnect = Integer.parseInt(daysToReconnectProp);
					
					Calendar calLastConnection = new GregorianCalendar( tz );
					calLastConnection.setTime(lastConnectionDate);
					
					Calendar calNextConnection = (Calendar) calLastConnection.clone();
					calNextConnection.add(Calendar.DAY_OF_MONTH, daysToReconnect);
					
					Calendar calNow = new GregorianCalendar( tz );
					calNow.setTime(new Date() );
					
					if( calNextConnection.before(calNow) )
					{
						contactFlickr = true;
					}
				}
				catch (ParseException e)
				{
					contactFlickr = true;
				}
			}
		}
		
		
		if( contactFlickr )
		{
			// Contact Flickr for new photo list
			String apiKey = props.getProperty( "apiKey" );
			String apiSecret = props.getProperty( "apiSecret" );
			
			if( apiKey == null || apiSecret == null )
			{
				// TODO: Missing properties
				return false;
			}
			
			REST rest = null;
			try
			{
				rest = new REST();
			}
			catch (ParserConfigurationException e)
			{
				e.printStackTrace();
				return false;
			}
			
		    Properties systemSettings = System.getProperties();
		    
		    //
		    // Look for a proxy setting
		    //
		    
		    String proxyHost = props.getProperty("proxyHost");
		    String proxyPortString;
		    
		    if( proxyHost != null && proxyHost != "" )
		    {
		    	proxyPortString = props.getProperty("proxyPort");
		    }
		    else
		    {
		    	proxyHost = (String) systemSettings.get("http.proxyHost" );
		    	proxyPortString = (String) systemSettings.get("http.proxyPort");
		    }
		    
		    if( proxyHost != null && proxyHost != "" )
		    {
				int proxyPort;
				try
				{
					proxyPort = Integer.parseInt(proxyPortString);
				}
				catch (NumberFormatException e1)
				{
					proxyPort = 8080; // Try a default.
				}
		    	
		    	rest.setProxy(proxyHost, proxyPort);
		    }
			
			// TODO Check Flickr only if we haven't done it since time ended. Only check once a week or some time.
			Flickr f = new Flickr( apiKey, apiSecret, rest );
			
			PeopleInterface pi = f.getPeopleInterface();
			
			String userToken = props.getProperty( "userToken" );
			String sPhotoCount = props.getProperty( "photoCount" );
			
			int photoCount;
			try
			{
				photoCount = Integer.parseInt(sPhotoCount);
			}
			catch (NumberFormatException e1)
			{
				// TODO Integer is not a number.
				e1.printStackTrace();
				return false;
			}
			
			if( photoCount > 500 )
				photoCount = 500;
			else if( photoCount < 1 )
				photoCount = 1;
				
			try
			{
				pl = pi.getPublicPhotos(userToken, photoCount,1);
				
				DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
				String today = dfm.format(new Date());
				
				props.setProperty("lastConnection", today);
			}
			catch( Exception e) //catch (IOException e), catch (SAXException e), catch (FlickrException e)
			{
				e.printStackTrace();
				return false;
			}
			
			// Write to disk with FileOutputStream
			FileOutputStream f_out = null;
			try
			{
				f_out = new FileOutputStream( programWorkingFolder + DATA_FILE );
			} catch (FileNotFoundException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	
			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = null;
			try
			{
				obj_out = new ObjectOutputStream (f_out);
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Write object out to disk
			try
			{
				obj_out.writeObject ( pl );
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try
			{
				if( f_out != null )
					f_out.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    	String downloadFolder = programWorkingFolder + DOWNLOADS_FOLDER;
		File downloads = new File( downloadFolder );
		if( !downloads.exists() )
			downloads.mkdirs();
		
		// TODO: Check if file already exists, but delete ones we aren't using any more.
		// Should only be less than the number of photos allowed to be downloaded some 
		// may need to delete before downloading 
	    for (Iterator<com.aetrion.flickr.photos.Photo> i = (Iterator<com.aetrion.flickr.photos.Photo>)pl.iterator(); i.hasNext();)
	    {
	    	com.aetrion.flickr.photos.Photo photo = i.next();
	    	
	    	URL url = null;
	    	
	    	try
			{
				url = new URL( photo.getLargeUrl() );//photo.getOriginalUrl() );
			} 
	    	catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
	    	/*catch (FlickrException e)
			{
				e.printStackTrace();
			}*/
	    	
			String fileName = url.getFile();
			fileName = fileName.substring( fileName.lastIndexOf('/') + 1 );
	    	
			if( !addByFilename( downloadFolder + fileName ) )
			{
				Download dl = new Download( url, downloadFolder );
				
				_downloads.add( dl );
				dl.addObserver(this);
				
				dl.download();
			}
	    }
	    
		props.saveProperties( propertiesFile );
	    
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		Download download = _downloads.get( _downloads.indexOf(o) );
		
		switch( download.getStatus() )	
		{
			case Download.DOWNLOADING:
			//case Download.PAUSED:
			case Download.CANCELLED:
				break;
				
			case Download.ERROR:
				// TODO: Do something with error
				break;
				
			case Download.COMPLETE:
			{
				download.deleteObservers();
				_downloads.remove(download);
				
				addByFilename( download.getDownloadedFilePosition() );
			}
		}
	}
	
	private boolean addByFilename( String filename )
	{
		return addByFile( new File( filename ) );
	}
	
	private boolean addByFile( File file )
	{
		if( file != null && file.exists() && file.length() != 2900 ) // TODO: File size of the dummy file that is returned if the real one is not available, need a better way!
		{
			uk.co.akademy.PhotoShow.Photo photo = new uk.co.akademy.PhotoShow.Photo(file);
			
			photo.setReady(true);
			havePhoto(photo);
			
			return true;
		}
		
		return false;
	}
}
