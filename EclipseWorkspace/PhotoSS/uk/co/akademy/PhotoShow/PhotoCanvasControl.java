/**
 * COntrolling which photo a photocanvas shows.
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * @author matthew
 *
 */
public class PhotoCanvasControl implements Runnable, Observer
{
	private ArrayList<PhotoCanvas> _photoCanvasList = null;
	private ArrayList<PhotosFrom> _photosFromList = null;

	private ArrayList<Photo> _photos = null;
	private ArrayList<Thread> _threads = null;
	
	private int _photoShowTime = 0;
	private int _photoCanvasCount;

	private boolean _debug = false;
	
	
	private volatile boolean _waitForPhotos = false;
	
	/**
	 * PhotoCanvasControl 
	 * @param pcs PhotoCanvas list to show the photos in
	 * @param pff PhotosFrom Where we are getting the photos from 
	 */
	public PhotoCanvasControl( ArrayList<PhotoCanvas> photoCanvasList, ArrayList<PhotosFrom> photosFromList )
	{
		_photos = new ArrayList<Photo>();
		_threads = new ArrayList<Thread>();
		
		_photosFromList = photosFromList;

		_photoCanvasList = photoCanvasList;
		_photoCanvasCount = _photoCanvasList.size();
		
		_photoShowTime = 8000;
		
		//_debug = true;
		
		_waitForPhotos = true;
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
		for( PhotosFrom pf : _photosFromList )
		{
			if( pf.initilise() )
			{
				pf.addObserver( this );
				
				Thread t = new Thread( pf );
				t.start();
				
				_threads.add( t );
			}
		}
		
		for( PhotoCanvas pc : _photoCanvasList )
			pc.setVisible(true);
		
		new Thread( this ).start();
			
		if( _threads.isEmpty() )
		{			
			for( PhotoCanvas pc : _photoCanvasList )
				pc.setNoPhotos( true );
			
			_waitForPhotos = false;
			
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
		while( _waitForPhotos )
		{
			// Haven't got an image yet so sleep for a bit.
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException e) { }
		}

		if( !_photos.isEmpty() )
		{
			ArrayList<Photo> photosCurrent =  null;
			
			String error = "";
			
			for(;;)
			{
				synchronized( _photos ) {
					photosCurrent = clonePhotos( _photos );
				};
				
				Collections.shuffle( photosCurrent );
				int total = photosCurrent.size();
				
				if( total < _photoCanvasCount )
					Collections.shuffle( _photoCanvasList ); // Shuffle so all canvases eventually get a photo
				
				int currentPhoto = 0;
				
				while( currentPhoto < total )
				{
					int photosToShow = Math.min( _photoCanvasCount, total - currentPhoto );
	
					int i;
					for( i = 0; i < photosToShow; i++ )
						_photoCanvasList.get(i).setNextPhoto( photosCurrent.get(i+currentPhoto) );
					
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

		synchronized( _photos ) {	
			_photos.add( photo ); // TODO... technically we should be taking a deep copy of the photo, but while this is a simple case (with only one watcher) we can ignore this
		}
		
		if( _waitForPhotos )
			_waitForPhotos = false;
	}
}