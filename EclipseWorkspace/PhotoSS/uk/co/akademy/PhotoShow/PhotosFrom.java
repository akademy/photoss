/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.util.Observable;
/**
 * @author Matthew
 *
 */
public abstract class PhotosFrom extends Observable
{
	/**
	 * Do anything that needs doing to get the photos
	 */
	abstract boolean Initilise();
	
	/**
	 * Notify anything that is watching that we have a photo.
	 * @param photo
	 */
	void havePhoto( Photo photo )
	{
		if( photo != null )
		{
			setChanged();
			notifyObservers( photo );
		}
	}
	
	boolean isPhoto( File f )
	{
		boolean isPhoto = false;
		
		if( f.isFile() )
		{
			String name = f.getName().toLowerCase();
	
			if( name.endsWith(".jpg") || name.endsWith( ".jpeg") ||
				name.endsWith(".gif") ||
				name.endsWith(".bmp") ||
				name.endsWith(".png") )
				isPhoto = true;
		}
				
		return isPhoto;
	}
}