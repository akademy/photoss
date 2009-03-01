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
public class PhotoCanvasControl implements Runnable, Observer
{
	private ArrayList<PhotoCanvas> _photoCanvasList = null;
	private ArrayList<PhotosFrom> _photosFromList = null;

	private ArrayList<Photo> _photos = null;

	private boolean _debug = false;
	/**
	 * PhotoCanvasControl 
	 * @param pcs PhotoCanvas list to show the photos in
	 * @param pff PhotosFrom Where we are getting the photos from 
	 */
	public PhotoCanvasControl( ArrayList<PhotoCanvas> photoCanvasList, ArrayList<PhotosFrom> photosFromList )
	{
		_photos = new ArrayList<Photo>();
		
		_photosFromList = photosFromList;
		
		for( PhotosFrom pf : _photosFromList )
			pf.addObserver( this );

		_photoCanvasList = photoCanvasList;
		
		//_debug = true;
		for( PhotoCanvas pc : _photoCanvasList )
			pc.setDebug( _debug );
	}

	/**
	 * Initilise all the PhotosFrom list
	 */
	public void initilise()
	{
		for( PhotosFrom pf : _photosFromList )
		{
			if( !pf.initilise() )
				pf.deleteObserver( this );
		}

		start();
	}

	/* (non-Javadoc)
	 * Swap the displayed photos around
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{	
		while( _photos.isEmpty() )
		{
			// Haven't got an image yet sleep for a bit.
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException e) { }
		}

		Random rand = new Random();
		Photo photoPrevious = null;
		ArrayList<Photo> photosToShow =  null;

		String error = "";
		
		for(;;)
		{
			photosToShow = clonePhotos( _photos );

			while( !photosToShow.isEmpty() )
			{
				int nPhoto = rand.nextInt( photosToShow.size() );
				Photo photo = photosToShow.remove( nPhoto );

				if( _debug )
				{
					error += nPhoto + " ";
					for( PhotoCanvas pc : _photoCanvasList )			
						pc.setDebugText(error);
				}
				
				try
				{
					photo.setImage( ImageIO.read( new ByteArrayInputStream ( photo.getBytes() ) ) );

					for( PhotoCanvas pc : _photoCanvasList )
					{
						pc.setNextPhoto( photo );
						pc.switchPhotoStart( 500 );
					}
				}
				catch (IOException e)
				{
					photo = null;
				}

				//if( photoPrevious != null && photoPrevious != photo )
				//	photoPrevious.setImage(null); // dereference the previous image.

				photoPrevious = photo;

				try {
					Thread.sleep( 5000 );
				} catch (InterruptedException e) { }
			}
		}
	}
	
	/**
	 * Return a shallow copy (clone) of a photo arraylist. This avoids a warning but keeps in to a small area
	 * @param photos the arraylist to clone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Photo> clonePhotos( ArrayList<Photo> photos )
	{
		return (ArrayList<Photo>) photos.clone();
	}

	/**
	 * Start up the main loop
	 */
	private void start()
	{
		Thread thread = new Thread(this);
		thread.start();
	}

	/* (non-Javadoc) Add a photo to the list
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

	/**
	 * Get the bytes of a file
	 * @param file The file to load
	 * @return An array of bytes
	 * @throws IOException 
	 */
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
        // Deflater compressor = new Deflater();
        // compressor.setLevel(Deflater.BEST_SPEED);

        return bytes;
    }
}