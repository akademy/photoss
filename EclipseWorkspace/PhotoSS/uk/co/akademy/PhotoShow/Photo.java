/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.awt.Image;
import java.io.File;
/**
 * @author matthew
 *
 */
public class Photo
{
	private static int _idGenerate = 1;
	
	private Image _image = null;
	private File _file;
	private byte[] _bytes;
	private int _id = 0;
	private boolean _ready = false;
	
	private Photo()
	{
		_id = _idGenerate++;		
	}
	
	public Photo(Image image)
	{
		this();
		_image = image;
	}
	
	public Photo( File file )
	{
		this();
		_file = file;
	}
	
	/**
	 * @return the _bReady
	 */
	public boolean isReady()
	{
		return _ready;
	}

	/**
	 * @param ready the _bReady to set
	 */
	public void setReady(boolean ready) 
	{
		_ready = ready;
	}

	/**
	 * @return the _id
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the _image
	 */
	public Image getImage() 
	{
		return _image;
	}

	/**
	 * @param _image the _image to set
	 */
	public void setImage(Image image)
	{
		if( this._image != null && this._image != image )
			this._image.flush();
		
		this._image = image;
	}
	/**
	 * 
	 * @return
	 */
	public int getHeight()
	{
		if( _image != null )
			return _image.getHeight(null);
		
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getWidth()
	{
		if( _image != null )
			return _image.getWidth(null);
		
		return 0;
	}

	/**
	 * @return the _file
	 */
	public File getFile()
	{
		return _file;
	}

	/**
	 * @param _file the _file to set
	 */
	public void setFile(File file)
	{
		this._file = file;
	}

	/**
	 * @return the _bytes
	 */
	public byte[] getBytes()
	{
		return _bytes;
	}

	/**
	 * @param _bytes the _bytes to set
	 */
	public void setBytes(byte[] _bytes)
	{
		this._bytes = _bytes;
	}
}
