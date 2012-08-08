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
public class PhotoCanvasControl implements Runnable
{
	private ShowControl _showControl;
	private ArrayList<PhotoCanvas> _photoCanvasList = null;
	private ArrayList<Photo> _photos = null;
	
	private int _photoShowTime = 8000;
	private int _photoCanvasCount;

	private boolean _debug = false;
	
	/**
	 * PhotoCanvasControl 
	 * @param pcs PhotoCanvas list to show the photos in
	 * @param pff PhotosFrom Where we are getting the photos from 
	 */
	public PhotoCanvasControl( ShowControl sc, ArrayList<PhotoCanvas> photoCanvasList )
	{
		this( sc, photoCanvasList, 8000 );
	}

	public PhotoCanvasControl( ShowControl sc, ArrayList<PhotoCanvas> photoCanvasList, int showTime )
	{
		_showControl = sc;

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
		// Set all canvases to visible
		for( PhotoCanvas pc : _photoCanvasList )
			pc.setVisible(true);
		
		// Run the photo swapping routine
		new Thread( this ).start();
	}
	
	/* (non-Javadoc)
	 * Swap the displayed photos around
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{	
		ArrayList<Photo> photosCurrent =  null;
		String debugText = "";
		
		for(;;)
		{
			photosCurrent = _showControl.photoRequest( _photoCanvasCount, false );

			int photosToShow = photosCurrent.size();

			int i;

			// Set a photo to show next
			for( i = 0; i < photosToShow; i++ ) {
				_photoCanvasList.get(i).setNextPhoto( photosCurrent.get(i) );
			 }

			// Switch the next photo to the current one
			for( i = 0; i < photosToShow; i++ )
				_photoCanvasList.get(i).switchPhotoStart( 500 );

			if( _debug )
			{
				for( PhotoCanvas pc : _photoCanvasList )
					pc.setDebugText(debugText);
			}

			try {
				Thread.sleep( _photoShowTime );
			} catch (InterruptedException e) { }
		}
	}
}