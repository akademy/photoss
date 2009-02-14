/**
 * 
 */
package uk.co.akademy.PhotoShow;

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
	abstract boolean initilise();
	
	/**
	 * Notify anything that is watching that we have a photo.
	 * @param photo
	 */
	void havePhoto( Photo photo )
	{
		if( photo != null && photo.isReady() )
		{
			setChanged();
			notifyObservers( photo );
		}
	}
}