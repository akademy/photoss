/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.util.Observable;
import uk.co.akademy.Downloader.Download;
import uk.co.akademy.Downloader.DownloadQueue;
/**
 * @author Matthew
 *
 */
public abstract class AbstractPhotosFrom extends Observable implements Runnable
{
	protected DownloadQueue _queue;
	/**
	 * Do anything that needs doing to get the photos
	 */
	abstract public boolean initilise();
	abstract public void run(); // From Runnable
	

	/**
	 * Notify anything that is watching that we have a photo.
	 * @param photo
	 */
	public void havePhoto( Photo photo )
	{
		if( photo != null )
		{
			setChanged();
			notifyObservers( photo );
		}
	}
	public void havePhoto( Download download )
	{
		if( download != null )
		{
			setChanged();
			notifyObservers( download );
		}
	}
	

	protected boolean isPhoto( File f )
	{
		boolean isPhoto = false;
		
		if( f.isFile() )
		{
			String name = f.getName().toLowerCase();
			int pos = name.lastIndexOf(".");
			
			if( pos != -1 )
			{
				String endswith = name.substring(pos+1);
				
				if( endswith.equals("jpg") || endswith.equals( "jpeg") ||
						endswith.equals("gif") ||
						endswith.equals("bmp") ||
						endswith.equals("png") 	)
					isPhoto = true;
			}
		}
				
		return isPhoto;
	}
}