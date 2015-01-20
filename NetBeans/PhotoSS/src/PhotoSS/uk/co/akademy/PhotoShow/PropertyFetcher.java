/**
 * Property helper class
 */
package uk.co.akademy.PhotoShow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.flickr4java.flickr.util.IOUtilities;

/**
 * @author Matthew
 *
 */
public class PropertyFetcher
{
	private Properties _properties = null;
    
    public PropertyFetcher()
    {
	    _properties = new Properties();
    }
    
	public String getProperty( String prop )
	{		
		return _properties.getProperty( prop );
	}
	
	public void setProperty( String prop, String value )
	{		
		_properties.setProperty( prop, value );
	}
	
	public void loadProperties( String filename ) throws IOException
	{
	    FileInputStream in = null;
	    try
	    {
	    	in = new FileInputStream(filename);
		    _properties.load(in);
		}
	    finally
	    {
		    IOUtilities.close(in);
		}
	}
	
	public void saveProperties( String filename )
	{
	    FileOutputStream out = null;
	    try
	    {
	    	out = new FileOutputStream( filename );
		    _properties.store( out, "Photoss" );
		}
	    catch( FileNotFoundException fnfe) {  }
	    catch( IOException ioe ) { }
	    finally
	    {
		    IOUtilities.close( out );
		}
	}
}
