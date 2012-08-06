/*
 * Interface for a show of photos
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;

public class Show {
	protected ArrayList<PhotoCanvas> _photoCanvasList = new ArrayList<PhotoCanvas>();
	/* 
	 * Do all the setting up of the show, avoid any visible changes.
	 */
	public boolean initilise() {
		PhotoCanvas pc = new PhotoCanvas( 300, 200 );

		_photoCanvasList.add( pc );
		pc.setVisible(true);

		return true;
	};

	/*
	 * Display and start the show
	 */
	public void start( ArrayList<AbstractPhotosFrom> photosFromList ) {
		if( !_photoCanvasList.isEmpty() ) {
			PhotoCanvasControl pcc = new PhotoCanvasControl( _photoCanvasList, photosFromList );

			pcc.initialise();
			pcc.start();
		}
	};
}
