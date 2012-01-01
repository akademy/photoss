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
public class PhotosFrom_Folder extends AbstractPhotosFrom
{
	private String[] _folders;
	
	public PhotosFrom_Folder()
	{
		super();
	}
	
	public boolean initilise()
	{
		String folders = Program.getProperty("folder.folders");
		
		_folders = folders.split( ";" );
		
		if( _folders.length > 0 )
		{
			for( String s : _folders )
				if( !s.trim().isEmpty() )
					return true;
		}
		
		return false;
	}
	
	public void run()
	{
		for( String folder : _folders )
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
				}
			}
		}
	}
}
