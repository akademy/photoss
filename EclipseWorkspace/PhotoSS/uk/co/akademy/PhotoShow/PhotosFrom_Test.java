/*
 * Test the program with a couple of files.
 */
package uk.co.akademy.PhotoShow;

import java.io.File;

public class PhotosFrom_Test extends AbstractPhotosFrom {

	@Override
	public boolean initilise()
	{
		return true;
	}
	
	public void run()
	{
		String fileName1 = "test1.jpg";
		String fileName2 = "test2.jpg";
		
        File file = new File(fileName1);
        File file2 = new File(fileName2);

		Photo photo1 = new Photo(file);
		Photo photo2 = new Photo(file2);
		
		havePhoto(photo1);
		havePhoto(photo2);
	}

}
