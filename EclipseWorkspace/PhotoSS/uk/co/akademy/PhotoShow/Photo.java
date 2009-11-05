/**
 * Holds some information about each of the photos.
 * Primarily the file location of the photo.
 */
package uk.co.akademy.PhotoShow;

import java.io.File;
/**
 * @author matthew
 *
 */
public class Photo
{
	private static int _idGenerate = 1;
	
	private File _file;
	private byte[] _bytes;
	private int _id = 0;
	
	private Photo()
	{
		_id = _idGenerate++;		
	}
	
	public Photo( File file )
	{
		this();
		_file = file;
		
		/*try {
			//getBytesFromFile( file );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/**
	 * @return the _id
	 */
	public int getId()
	{
		return _id;
	}

	public File getFile()
	{
		return _file;
	}
	
	/**
	 * @param _file the _file to set
	 */
	public void setData( File file )
	{
		/*try {
			//getBytesFromFile( file );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/**
	 * @return the _bytes
	 */
	public byte[] getBytes()
	{
		return _bytes;
	}
}
