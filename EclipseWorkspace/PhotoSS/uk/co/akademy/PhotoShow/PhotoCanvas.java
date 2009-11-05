/**
 * A canvas that the image of a photo is drawn to.
 */
package uk.co.akademy.PhotoShow;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Matthew
 *
 */
public class PhotoCanvas extends Canvas
{
	private static final long serialVersionUID = 8302867124480794953L;

	private String _debugText = ""; 
	private boolean _debug = false;
	
	private Photo _photoCurrent = null;
	private Photo _photoNext = null;
	
	private int _width = 0, _height = 0;

	private Image _screenBuffer = null;
	private Image _image, _imageNext = null;

	public PhotoCanvas()
	{
		this.setBackground(Color.black);
		this.setEnabled(false);
	}

	public void setController( PhotoCanvasControl pcc ) {}
	
	public void setDebug( boolean debug ) { _debug = debug; }
	public void setDebugText( String debugText ) { _debugText = debugText; }
	
	public void setNextPhoto( Photo photo )
	{
		_photoNext = photo;
		
		try {
			_imageNext = ImageIO.read(_photoNext.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void switchPhotoStart( int fadeLength )
	{
		showNextPhoto();
		repaint();
	}
	
	private void showNextPhoto()
	{		
		_photoCurrent = _photoNext;
		
		Image imagePrevious = _image;
		_image = _imageNext;
		
		if( imagePrevious != null )
			imagePrevious.flush();
	}
	
	@Override
	public boolean isDoubleBuffered()
	{
		return true;
	}
	
	/**
	 *  @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics graphic)
	{
		if( _width != this.getWidth() || _height != this.getHeight() )
		{
			_width = this.getWidth();
			_height = this.getHeight();

			if( _screenBuffer != null )
			{
				_screenBuffer.flush();
				_screenBuffer = null;
			}

			_screenBuffer = createImage( _width,_height );
		}

		Graphics screenBufferGraphic = _screenBuffer.getGraphics();

		//
		// Draw the stuff
		//
		
		super.paint(screenBufferGraphic);
		screenBufferGraphic.clearRect( 0, 0, _width, _height );

		if( _photoCurrent != null )
		{
			//
			// Simple drawing, left in for debugging - just draw it top left
			//
			// graphic.drawImage( _image, 0, 0, null);
			
			
			//
			// Centre image
			//			
			float 	drawWidth = (float) _image.getWidth(null), 
					drawHeight = (float) _image.getHeight(null);
			
			float	posX = 0, posY = 0;
						
			if( drawWidth > _width || drawHeight > _height )
			{
				// Image larger than available area
				if( drawWidth > drawHeight )
				{
					// landscape
					drawHeight = drawHeight * ( _width / drawWidth );
					drawWidth = _width;
					posY = ( _height - drawHeight ) / 2;
				}
				else
				{
					// portrait
					drawWidth = drawWidth * ( _height / drawHeight );
					drawHeight = _height;
					posX = ( _width - drawWidth ) / 2;
				}
			}
			else
			{
				posX = ( _width - drawWidth ) / 2;
				posY = ( _height - drawHeight ) / 2;				
			}

			screenBufferGraphic.drawImage( _image, (int)posX, (int)posY, (int)drawWidth, (int)drawHeight, null );
		}
		else
		{
			screenBufferGraphic.setColor( Color.white );
			screenBufferGraphic.drawString( "Getting photos...", 20, 20 );
		}
		
		if( _debug )
		{
			screenBufferGraphic.setXORMode( Color.white );
			screenBufferGraphic.drawString( _debugText, 20, 40 );
		}
		
		//
		// Paint to on screen
		//
		graphic.drawImage( _screenBuffer, 0, 0, this );
		screenBufferGraphic.dispose();
	}
	
	/**
	 * 
	 */
	@Override
	public void update( Graphics g )
	{
		paint(g);
	}
}
