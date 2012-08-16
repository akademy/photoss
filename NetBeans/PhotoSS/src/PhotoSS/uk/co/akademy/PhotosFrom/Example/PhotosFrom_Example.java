/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.akademy.PhotosFrom.Example;

import java.io.File;
import uk.co.akademy.PhotoShow.AbstractPhotosFrom;
import uk.co.akademy.PhotoShow.Photo;

/**
 *
 * @author matthew
 */
public class PhotosFrom_Example extends AbstractPhotosFrom {

	private String[] _photos;

	public PhotosFrom_Example() { super(); }

	@Override
	public boolean initilise() {
		_photos = new String[2];

		_photos[0] = "/home/matthew/code/PhotoSS/photoss/NetBeans/PhotoSS/build/classes/example1.jpg";
		_photos[1] = "/home/matthew/code/PhotoSS/photoss/NetBeans/PhotoSS/build/classes/example2.jpg";

		return (_photos.length == 2);
	}

	@Override
	public void run() {

		for (String photofilename : _photos) {

			File file = new File( photofilename );

			Photo photo = new Photo(file);

			havePhoto(photo);
		}

	}

}
