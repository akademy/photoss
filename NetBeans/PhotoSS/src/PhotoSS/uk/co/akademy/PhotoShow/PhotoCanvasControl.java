/**
 * COntrolling which photo a photocanvas shows.
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * @author matthew
 *
 */
public class PhotoCanvasControl implements Runnable, Observer
{
	private ArrayList<PhotoCanvas> _photoCanvasList = null;
	private ArrayList<AbstractPhotosFrom> _photosFromList = null;

	private ArrayList<Photo> _photos = null;
	private ArrayList<Thread> _threads = null;
	
	private int _photoShowTime = 0;
	private int _photoCanvasCount;

	private boolean _debug = false;
	
	/**
	 * PhotoCanvasControl 
	 * @param pcs PhotoCanvas list to show the photos in
	 * @param pff PhotosFrom Where we are getting the photos from 
	 */
	public PhotoCanvasControl( ArrayList<PhotoCanvas> photoCanvasList, ArrayList<AbstractPhotosFrom> photosFromList )
	{
		_photos = new ArrayList<Photo>();
		_threads = new ArrayList<Thread>();
		
		_photosFromList = photosFromList;

		_photoCanvasList = photoCanvasList;
		_photoCanvasCount = _photoCanvasList.size();
		
		_photoShowTime = 8000;
		
		//_debug = true;
	}

	public PhotoCanvasControl( ArrayList<PhotoCanvas> photoCanvasList, int showTime )
	{
		_photos = new ArrayList<Photo>();

		_photoCanvasList = photoCanvasList;
		_photoCanvasCount = _photoCanvasList.size();
		
		_photoShowTime = showTime;
		
		//_debug = true;
	}

	/**
	 * Initialise all the PhotosFrom list
	 */
	public void initialise()
	{
		String imageShowTimeString = Program.getProperty("general.photoShowTime");
		try
		{
			_photoShowTime = Integer.parseInt(imageShowTimeString);
		}
		catch (NumberFormatException e1) {}
		
		for( PhotoCanvas pc : _photoCanvasList )
		{
			pc.setController( this );
			pc.setDebug( _debug );
		}
	}

	/**
	 * Start up the main loop
	 */
	public void start()
	{
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
		
		// Set all canvases to visible
		for( PhotoCanvas pc : _photoCanvasList )
			pc.setVisible(true);
		
		// Run the main photo swapping page
		new Thread( this ).start();
			
		
		if( _threads.isEmpty() )
		{			
			for( PhotoCanvas pc : _photoCanvasList )
				pc.setNoPhotos( true );
			
			try {
				Thread.sleep( _photoShowTime );
			} catch (InterruptedException e) { }
		}
		else
		{
			//TODO: Monitor threads so if all finish with no photos we can notify user.
		}
	}
	
	/* (non-Javadoc)
	 * Swap the displayed photos around
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{	
		boolean havePhotos;
		
		// Wait for some photos
		for(;;) {
			synchronized( _photos ) {
				havePhotos = !_photos.isEmpty();
			}

			if( havePhotos )
				break;
			
			// Haven't got an image yet so sleep for a bit.
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException e) { }
		}
		
		ArrayList<Photo> photosCurrent =  null;
		String error = "";
		
		// Now loop through our photos (it expands as more photos come available)
		for(;;)
		{
			synchronized( _photos ) {
				photosCurrent = clonePhotos( _photos );
			};

			Collections.shuffle( photosCurrent );

			int total = photosCurrent.size();
			if( total < _photoCanvasCount )
				Collections.shuffle( _photoCanvasList ); // Shuffle so all canvases eventually get a photo if there's less photos than canvases

			int currentPhoto = 0;

			while( currentPhoto < total )
			{
				int photosToShow = Math.min( _photoCanvasCount, total - currentPhoto );

				int i;
				// Set a photo to show next
				for( i = 0; i < photosToShow; i++ ) {

					if( _debug ) {
					  System.out.println("Image number: " + (i+currentPhoto) );
					}
					_photoCanvasList.get(i).setNextPhoto( photosCurrent.get(i+currentPhoto) );
				 }

				// Switch the next photo to the current one
				for( i = 0; i < photosToShow; i++ )
					_photoCanvasList.get(i).switchPhotoStart( 500 );

				currentPhoto += photosToShow;

				if( _debug )
				{
					for( PhotoCanvas pc : _photoCanvasList )			
						pc.setDebugText(error);
				}	

				try {
					Thread.sleep( _photoShowTime );
				} catch (InterruptedException e) { }
			}
		}
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