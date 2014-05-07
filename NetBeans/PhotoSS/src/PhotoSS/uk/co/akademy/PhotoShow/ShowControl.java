/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.akademy.PhotoShow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author matthew
 */
public class ShowControl implements Observer {
	
	private ArrayList<AbstractPhotosFrom> _photosFromList = null;

	private final ArrayList<Photo> _photos = new ArrayList<Photo>();

	private ArrayList<Photo> _photosQueue = null;
	private ArrayList<Thread> _threads = null;

	private PhotoCanvasControl _photoCanvasControl;

	private boolean _debug = false;
	private Show _show = null;


	public ShowControl( Show show, ArrayList<AbstractPhotosFrom> photosFromList )
	{
		_show = show;
		_photosFromList = photosFromList;
		
		_photosQueue = new ArrayList<Photo>(0);

		_threads = new ArrayList<Thread>();


		//_debug = true;
	}

	/**
	 * Start up the main loop
	 */
	public boolean start()
	{
		if( _show.getPhotoCanvasList().isEmpty() || _photosFromList.isEmpty() ) {
			
			return false;
		}
		
		_photoCanvasControl = new PhotoCanvasControl(this, _show.getPhotoCanvasList(), 8000);

		// Create a thread for each PhotoFrom to run in and collect photos
		for( AbstractPhotosFrom pf : _photosFromList )
		{
			if( pf.initilise() )
			{
				pf.addObserver( this );

				Thread t = new Thread( pf );
				t.start();

				_threads.add( t );
			}
		}

		_photoCanvasControl.initialise();
		_photoCanvasControl.start();
		
		_show.start();	
		
		return true;
	}


	// TODO: Need PhotoCanvasControl to request a number of photos...
	public ArrayList<Photo> photoRequest( int photoRequestNumber, boolean wait ) {

		ArrayList<Photo> requestQueue = new ArrayList<Photo>(0);

		if( _photosQueue.isEmpty() ) {

			synchronized( _photos ) {
				_photosQueue = clonePhotos( _photos );
			}

			Collections.shuffle( _photosQueue );
		}

		if( wait && photoRequestNumber > _photosQueue.size() ) {
			// TODO: wait for more images
		}

		if( !_photosQueue.isEmpty() ) {
			int photoRequestNumberLimit = Math.min(photoRequestNumber, _photosQueue.size() );

			for( int i = 0; i < photoRequestNumberLimit; i++ ) {
				requestQueue.add(_photosQueue.remove(0) );
			}
		}

		return requestQueue;
	}

	/**
	 * Return a shallow copy (clone) of a photo arraylist. This avoids a warning but keeps in to a small area
	 * @param photos the arraylist to clone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Photo> clonePhotos( ArrayList<Photo> photos )
	{
		return (ArrayList<Photo>) photos.clone();
	}

	/* (non-Javadoc) Add a photo to the list
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable oPhotosFrom, Object oPhoto )
	{
		Photo photo = (Photo)oPhoto;

		if( photo.good() ) {

			synchronized( _photos ) {
				_photos.add( photo ); // TODO... technically we should be taking a deep copy of the photo, but while this is a simple case (with only one watcher) we can ignore this
			}
		}
	}
}
