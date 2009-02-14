/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
//import java.util.zip.Deflater;

import javax.imageio.ImageIO;

/**
 * @author matthew
 *
 */
public class PhotoCanvasControl implements Runnable, Observer {

	private PhotoCanvas _photoCanvas = null;
	private PhotosFrom _photoFrom = null; 
	
	private ArrayList<Photo> _photos;

	public PhotoCanvasControl( PhotoCanvas pc, PhotosFrom pff )
	{
		_photoFrom = pff;
		_photoFrom.addObserver( this );
		
		_photoCanvas = pc;
		_photos = new ArrayList<Photo>();
	}
	
	public void initilise()
	{
		if( !_photoFrom.initilise() )
			_photoFrom.deleteObserver( this );
		
		start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{	
		Random rand = new Random();
		Photo photoPrevious = null;
		
		while( true )
		{
			if( !_photos.isEmpty() )
			{
				int iNextPhoto = rand.nextInt( _photos.size() );
				Photo photoNext = _photos.get( iNextPhoto );
				
				// Check we have a different one.
				if( photoNext == photoPrevious )
				{
					if( iNextPhoto + 1 ==  _photos.size() )
						photoNext = _photos.get( 0 );
					else
						photoNext = _photos.get( iNextPhoto + 1  );
				}
				
				try
				{
					photoNext.setImage( ImageIO.read ( new ByteArrayInputStream ( photoNext.getBytes() ) ) );
		
					_photoCanvas.setNextPhoto( photoNext );
					_photoCanvas.switchPhoto();
				}
				catch (IOException e)
				{
					photoNext = null;
				}
				
				// dereference the previous image.
				if( photoPrevious != null )
					photoPrevious.setImage(null);
				
				photoPrevious = photoNext;
				
				try {
					Thread.sleep( 10000 );
				} catch (InterruptedException e) {
					//System.exit(0);
				}
			}
			else
			{
				// Haven't got an image yet sleep for a bit.
				try {
					Thread.sleep( 100 );
				} catch (InterruptedException e) {
					//System.exit(0);
				}
			}
		}
	}
	
	private void start()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable oPhotosFrom, Object oPhoto )
	{
		//PhotosFrom pff = (PhotosFrom)o;
		Photo photo = (Photo)oPhoto;
		
		try
		{
			photo.setBytes( getBytesFromFile( photo.getFile() ) );
			
			_photos.add( photo ); // TODO... technically we should be taking a deep copy of the photo, but as this is a pretty simple (with only one watcher) we can ignore this... until... we can't...
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
    public static byte[] getBytesFromFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+ file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        
        // TODO Compress images with no built in compression: BMP, TIF
        //Deflater compressor = new Deflater();
        //compressor.setLevel(Deflater.BEST_SPEED);
        
        return bytes;
    }
}
