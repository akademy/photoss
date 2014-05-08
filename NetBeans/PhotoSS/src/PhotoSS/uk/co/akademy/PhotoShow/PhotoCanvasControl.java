/**
 * COntrolling which photo a photocanvas shows.
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;


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

		// TODO : implement a wait / notify system rather than polling photosCurrent
		for(;;) {
			// wait for at least one photo
			photosCurrent = _showControl.photoRequest( _photoCanvasCount, false );

			if( photosCurrent.isEmpty() ) {
				try {
						Thread.sleep( 50 );
				} catch (InterruptedException e) { }
			}
			else {
				break;
			}
		}
		
		setNext( photosCurrent );
		
		for(;;)	{

			// Switch the next photo to the current one
			for( PhotoCanvas pc : _photoCanvasList ) {
				pc.switchPhotoStart( 500 );
			}

			long startTime = System.currentTimeMillis();

			if( _debug ) {
				for( PhotoCanvas pc : _photoCanvasList )
					pc.setDebugText(debugText);
			}

			photosCurrent = _showControl.photoRequest( _photoCanvasCount, false );

			setNext( photosCurrent );

			long endTime = System.currentTimeMillis();
			
			long timeLeft = _photoShowTime - (endTime - startTime);

			if( timeLeft > 0 ) {
				try {
					Thread.sleep( timeLeft );
				} catch (InterruptedException e) { }
			}
		}
	}

	private void setNext( ArrayList<Photo> photosCurrent ) {

		if( !photosCurrent.isEmpty() ) {

			// Set a photo to show next
			int iPhotos = photosCurrent.size();
			int iPhoto = 0;

			for( PhotoCanvas pc : _photoCanvasList ) {
				pc.setNextPhoto( photosCurrent.get( iPhoto % iPhotos ) );
				iPhoto++;
			}
		}
	}
}