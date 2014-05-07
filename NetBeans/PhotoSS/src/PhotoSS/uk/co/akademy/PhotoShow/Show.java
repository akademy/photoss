/*
 * Interface for a show of photos
 */
package uk.co.akademy.PhotoShow;

import java.util.ArrayList;

abstract public class Show {
	
	protected ArrayList<PhotoCanvas> _photoCanvasList = new ArrayList<PhotoCanvas>();
	/* 
	 * Do all the setting up of the show, avoid any visible changes.
	 */
	abstract public boolean initilise();
	abstract public void start();
	
	public ArrayList<PhotoCanvas> getPhotoCanvasList() { return _photoCanvasList; }
}
