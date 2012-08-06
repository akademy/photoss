/*
 * Interface for a show of photos
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;

public interface Show {
	/* 
	 * Do all the setting up of the show, avoid any visible changes.
	 */
	public boolean initilise();
	
	/*
	 * Display and start the show
	 */
	public void start( ArrayList<AbstractPhotosFrom> photosFromList );
}
