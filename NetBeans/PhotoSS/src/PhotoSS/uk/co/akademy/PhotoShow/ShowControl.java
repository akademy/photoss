/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.akademy.PhotoShow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import uk.co.akademy.Downloader.Download;
import uk.co.akademy.Downloader.DownloadQueue;

/**
 *
 * @author matthew
 */
public class ShowControl implements Observer {
	
	private ArrayList<AbstractPhotosFrom> _photosFromList = null;

	private final ArrayList<Photo> _photos = new ArrayList<Photo>();

	private DownloadQueue _downloadQueue = null;
	final private String _downloadFolderName = "_temp_downloads_";
	
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

	private String getDownloadFolder() {
		
		String programWorkingFolder = Program.getFolder();
		String downloadFolder = System.getProperty("java.io.tmpdir");
		
		if( downloadFolder.isEmpty() )
			downloadFolder = programWorkingFolder + _downloadFolderName;
		else
			downloadFolder += File.separator + _downloadFolderName;
		
		
		File checkFolder = new File( downloadFolder );
		if( !checkFolder.exists() )
			checkFolder.mkdirs();
		
		return downloadFolder;
	}
	
	/**
	 * Start up the main loop
	 */
	public boolean start()
	{
		if( _show.getPhotoCanvasList().isEmpty() || _photosFromList.isEmpty() ) {
			
			return false;
		}
		
		_downloadQueue = new DownloadQueue( this.getDownloadFolder() );
		_downloadQueue.addObserver(this);
			 
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

		_photoCanvasControl = new PhotoCanvasControl( this, _show.getPhotoCanvasList(), 8000 );
		_photoCanvasControl.initialise();
		
		_photoCanvasControl.start();
		
		Thread t = new Thread( _downloadQueue );
		t.start();
		
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
	public void update(Observable oObserve, Object o )
	{
		if ( o instanceof Download ) {
			
			_downloadQueue.addDownload( (Download)o, ((AbstractPhotosFrom)oObserve).getClass().getName() );
		}
		else if( o instanceof Photo ) {
			
			// oObserve might be photoFrom or DownloadQueue
			
			Photo photo = (Photo)o;

			if( photo.good() ) {

				synchronized( _photos ) {
					_photos.add( photo ); // TODO... technically we should be taking a deep copy of the photo, but while this is a simple case (with only one watcher) we can ignore this
				}
			}
		}
	}
}
