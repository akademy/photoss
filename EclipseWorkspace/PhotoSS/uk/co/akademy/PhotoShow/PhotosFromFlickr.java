/**
 * DOwnload some photos from flickr
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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import uk.co.akademy.Downloader.Download;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.Photosets;
import com.aetrion.flickr.photosets.PhotosetsInterface;

/**
 * @author Matthew
 *
 */
public class PhotosFromFlickr extends PhotosFrom implements Observer
{
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
	public boolean Initilise()
	{				
		String programWorkingFolder = Program.getFolder();
		File dataFile = new File(programWorkingFolder + DATA_FILE);
		
		PhotoList photoListPrevious = GetPreviousPhotoList( dataFile );
		PhotoList newPhotoList = null;
		
		boolean contactFlickr = false;

		// No saved photo list found, must access flickr data.
		if( photoListPrevious == null )
			contactFlickr = true;
		
		if( !contactFlickr )
		{
			String lastConnectionProp = Program.getProperty("flickr.lastConnection");				
			String daysToReconnectProp = Program.getProperty("flickr.daysToReconnect");
			
			contactFlickr = CheckPastUpdateDate( lastConnectionProp, daysToReconnectProp );
		}
		
		if( contactFlickr )
		{
			newPhotoList = GetPhotoListFromFlickr();

			if( newPhotoList != null )
			{
				//
				// Set the new contact date.
				//
				DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
				String today = dfm.format(new Date());
				
				Program.setProperty("flickr.lastConnection", today);
				
				//
				// Save the new list of photos
				//
				FileOutputStream f_out = null;
				try
				{
					f_out = new FileOutputStream( programWorkingFolder + DATA_FILE );
					ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
					obj_out.writeObject ( newPhotoList );
				}
				catch (FileNotFoundException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finally
				{
					if( f_out != null )
					{
						try {
							f_out.close();
						} catch (IOException e) {}
					}
				}
			}
		}

		PhotoList photoListToShow = photoListPrevious;
		
		if( newPhotoList != null )
			photoListToShow = newPhotoList;
			
		if( photoListToShow == null )
			return false;
		
		//
		// Add the photos, download if necessary?
		//
		String downloadFolder = programWorkingFolder + DOWNLOADS_FOLDER;
		File downloads = new File( downloadFolder );
		
		if( !downloads.exists() )
			downloads.mkdirs();
		
		// TODO: Check if file already exists, but delete ones we aren't using any more.
		// Should only be less than the number of photos allowed to be downloaded some 
		// may need to delete before downloading 
	    for (Iterator<com.aetrion.flickr.photos.Photo> i = (Iterator<com.aetrion.flickr.photos.Photo>)photoListToShow.iterator(); i.hasNext();)
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
	    	
			String fileName = url.getFile();
			fileName = fileName.substring( fileName.lastIndexOf('/') + 1 );
	    	
			if( !addByFilename( downloadFolder + fileName ) )
			{
				// TODO: Limit numbers of simultaneous downloads.
				Download dl = new Download( url, downloadFolder );
				
				_downloads.add( dl );
				dl.addObserver(this);
				
				dl.download();
			}
	    }

	    
		return true;
	}
	
	// Contact Flickr for new photo list
	private PhotoList GetPhotoListFromFlickr()
	{	
		String apiKey = Program.getProperty( "flickr.apiKey" );
		String apiSecret = Program.getProperty( "flickr.apiSecret" );

		if( ( apiKey == null ) || ( apiSecret == null ) ||
			( apiSecret == "" ) || ( apiKey == "" ) )
		{
			// TODO: Missing properties error handle.
			return null;
		}

		REST rest = null;
		try
		{
			rest = new REST();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
		
	    Properties systemSettings = System.getProperties();
	    
	    //
	    // Look for a proxy setting
	    //		    
	    String proxyHost = Program.getProperty("general.proxyHost");
	    String proxyPortString = "";
	    
	    if( proxyHost != null && proxyHost != "" )
	    {
	    	proxyPortString = Program.getProperty("general.proxyPort");
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
	    
		String sPhotoCount = Program.getProperty( "flickr.photoCount" );
		int photoCount;
		try
		{
			photoCount = Integer.parseInt(sPhotoCount);
		}
		catch (NumberFormatException e1)
		{
			// TODO Integer is not a number.
			e1.printStackTrace();
			return null;
		}
		
		if( photoCount > 500 )
			photoCount = 500;
		else if( photoCount < 1 )
			photoCount = 1;
	    
		//
	    // Connect to flickr
	    //
		Flickr flickr = new Flickr( apiKey, apiSecret, rest );
		
		PhotoList newPhotoList = null;
		
		String userToken = Program.getProperty( "flickr.userToken" );
		String sPhotosets = Program.getProperty( "flickr.photosets" );
		
		if( sPhotosets != "" )
		{
			PhotosetsInterface photosetsI = flickr.getPhotosetsInterface();
			
			Photosets photosets = null;
			
			try
			{
				photosets = photosetsI.getList(userToken);
			}
			catch (Exception e) //IOException e2, SAXException e2, FlickrException e2) {
			{
				e.printStackTrace();
				return null;
			}
			
			if( photosets != null )
			{
				// If we have a list of photosets, just get the photos from there.
				Collection<Photoset> photosetCollection = photosets.getPhotosets();
			
				String[] photosetsNamesArray = sPhotosets.split(";");
				for( Photoset photoset : photosetCollection )
				{
					String photosetName = photoset.getTitle();
					for( int i = 0; i<photosetsNamesArray.length;i++)
						if( photosetsNamesArray[i] != "" && 
								photosetsNamesArray[i].compareToIgnoreCase(photosetName) == 0 )
						{
							try
							{
								newPhotoList = photosetsI.getPhotos(photoset.getId(), photoCount / photosetsNamesArray.length, 1);
							}
							catch( Exception e) //catch (IOException e), catch (SAXException e), catch (FlickrException e)
							{
								e.printStackTrace();
								return null;
							}
						}
				}
			}
		}
		else
		{
			PeopleInterface pi = flickr.getPeopleInterface();
			
			try
			{
				newPhotoList = pi.getPublicPhotos(userToken, photoCount,1);
			}
			catch( Exception e) //catch (IOException e), catch (SAXException e), catch (FlickrException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		return newPhotoList;
	}

	// Check if we need to update the list.
	private boolean CheckPastUpdateDate( String lastConnection, String daysToConnect )
	{
		boolean pastDate = false;
		
		if( lastConnection == "" )
		{
			// No date of last connections
			pastDate = true;
		}
		else
		{
			TimeZone tz = null;//TimeZone.getTimeZone( "UTC" ); // This causes the debugger to wrongly stop, so lets use the default one for now...
			tz = TimeZone.getDefault();
			
			DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
			Date lastConnectionDate;
			
			try
			{
				lastConnectionDate = dfm.parse(lastConnection);	
				
				int daysToReconnect = Integer.parseInt(daysToConnect);
				
				Calendar calLastConnection = new GregorianCalendar( tz );
				calLastConnection.setTime(lastConnectionDate);
				
				Calendar calNextConnection = (Calendar) calLastConnection.clone();
				calNextConnection.add(Calendar.DAY_OF_MONTH, daysToReconnect);
				
				Calendar calNow = new GregorianCalendar( tz );
				calNow.setTime(new Date() );
				
				if( calNextConnection.before(calNow) )
				{
					pastDate = true;
				}
			}
			catch (ParseException e)
			{
				// Date error, try an update.
				pastDate = true;
			}
		}
		
		return pastDate;
	}

	private PhotoList GetPreviousPhotoList(File dataFile)
	{
		PhotoList photoList = null;
		
		if( dataFile != null && dataFile.exists() )
		{
			//
			// Read the saved photolist if it exists.
			//
			FileInputStream fileStream = null;
			try
			{
				fileStream = new FileInputStream( dataFile );
				ObjectInputStream objectStream = new ObjectInputStream(fileStream);
				
				Object object = objectStream.readObject();
				
				if (object instanceof PhotoList)
					photoList = (PhotoList) object;
			} 
			catch (FileNotFoundException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			catch (IOException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} 
			catch (ClassNotFoundException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			finally
			{
				if( fileStream != null )
				{
					try {
						fileStream.close();
					} catch (IOException e) {}
				}
			}
		}
		
		return photoList;
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
			
			havePhoto(photo);
			
			return true;
		}
		
		return false;
	}
}
