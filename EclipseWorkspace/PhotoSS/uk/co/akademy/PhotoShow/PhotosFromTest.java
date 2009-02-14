package uk.co.akademy.PhotoShow;

import java.io.File;

public class PhotosFromTest extends PhotosFrom {

	@Override
	public boolean initilise()
	{
		String fileName1 = "test.jpg";
		String fileName2 = "test2.jpg";
		
        File file = new File(fileName1);
        File file2 = new File(fileName2);

		Photo photo1 = new Photo(file);
		Photo photo2 = new Photo(file2);
		
		photo1.setReady(true);
		photo2.setReady(true);
		
		havePhoto(photo1);
		havePhoto(photo2);
		
		return true;
	}

}
