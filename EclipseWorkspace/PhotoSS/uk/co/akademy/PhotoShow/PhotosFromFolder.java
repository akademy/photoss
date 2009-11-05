/**
 * Load photos in specific local folders
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
import java.io.FileFilter;

/**
 * @author matthew
 *
 */
public class PhotosFromFolder extends PhotosFrom
{
	public PhotosFromFolder()
	{
		super();
	}
	
	public boolean Initilise()
	{
		String folders = Program.getProperty("folder.folders");
		
		String [] aFolders = folders.split( ";" );

		int photoCount = 0;
		for( String folder : aFolders )
		{
			File file = new File( folder );
			
			if( file.isDirectory() )
			{
				File[] photos = file.listFiles( new FileFilter() {
					public boolean accept( File f ) { return isPhoto( f ); }
				} );
				
				for( File filePhoto : photos )
				{
					Photo photo = new Photo(filePhoto);
						
					havePhoto(photo);
					
					photoCount++;
				}
			}
		}
		
		return ( photoCount > 0 );
	}
}
