/**
 * A canvas that the image of a photo is drawn to.
 */
package uk.co.akademy.PhotoShow;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Matthew
 *
 */
public class PhotoCanvas extends Canvas implements ComponentListener
{
	private static final long serialVersionUID = 8302867124480794953L;

	private String _debugText = ""; 
	private boolean _debug = false;
	
	private boolean _adjusting = false;
	private boolean _noPhotos = false;
	
	private int _widthCanvas = 0, _heightCanvas = 0;
	private int _widthImage = 0, _heightImage = 0;
	
	private int _margin = 25;
	private int _border = 5;
	
	private int _widthDraw = 0, _heightDraw = 0;
	private int _posX = 0, _posY = 0;

	private Image _screenBuffer = null;
	private Image _image = null, _imageNext = null;

	public PhotoCanvas( int width, int height )
	{
		this.addComponentListener(this);
		
		this.setBackground(Color.black);
		
		this.setSize( width, height );
		
		this.setEnabled(false);
		this.enableInputMethods(false);
		this.setFocusable(false);
		
		_widthCanvas = width;
		_heightCanvas = height;
	}
	
	public void setController( PhotoCanvasControl pcc ) {}
	
	public void setDebug( boolean debug ) { _debug = debug; }
	public void setDebugText( String debugText ) { _debugText = debugText; }
	
	public void setNoPhotos( boolean noPhotos ) { _noPhotos = noPhotos; repaint(); }
	
	public void setNextPhoto( Photo photo )
	{
		try {
			_imageNext = ImageIO.read( photo.getFile() );
		} 
		catch (IOException e)
		{
			 if( _debug )
				System.out.println("Caught IO exception!");
			//e.printStackTrace();
		}

		if( _debug ) {
			 if( _imageNext == null ) {
					 System.out.println("_imageNext is null!");
			 }
		 }
	}

	public void switchPhotoStart( int fadeLength )
	{
		showNextPhoto();
		repaint();
	}
	
	private void showNextPhoto()
	{
		Image imagePrevious = _image;
		
		_adjusting = true;
		
		_image = null;
		
		if(_imageNext != null ) {
			 _widthImage = _imageNext.getWidth( null );
			 _heightImage = _imageNext.getHeight( null );

			 calculateSizeAndPosition();

			 _image = _imageNext;

			 _adjusting = false;

			 if( imagePrevious != null )
				 imagePrevious.flush();
		 }
		  else {
			 if( _debug )
				System.out.println("Next image is null???");
		 }
	}
	
	public void calculateSizeAndPosition()
	{		
		float 	drawWidth = 0, 
				drawHeight = 0,
				posX = 0, posY = 0;

		float paintWidth = _widthImage + (_margin * 2),
		      paintHeight = _heightImage + (_margin * 2 );

		if( paintWidth <= _widthCanvas && paintHeight <= _heightCanvas )
		{
			// Image smaller
			_debugText = "Image smaller";
			
			drawWidth = (float) _widthImage;
			drawHeight = (float) _heightImage;
			
			posX = ( ( _widthCanvas - paintWidth ) / 2 ) + _margin;
			posY = ( ( _heightCanvas - paintHeight ) / 2 ) + _margin;
		}
		else 
		{
			if( paintWidth > _widthCanvas && paintHeight > _heightCanvas )
			{
				// Image wider and taller
				
				if( paintWidth > paintHeight )
				{
					_debugText += " - photo wide";
					drawWidth = (float)(_widthCanvas - (_margin * 2));
					drawHeight = _heightImage * ( drawWidth / _widthImage );
					
					if( drawHeight + (_margin * 2) > _heightCanvas )
					{
						drawHeight = (float)_heightCanvas - (_margin * 2);
						drawWidth = _widthImage * ( ((float)drawHeight) / _heightImage );
					}
				}
				else // ( paintHeight > paintWidth )
				{
					drawHeight = (float)(_heightCanvas - (_margin * 2));
					drawWidth = _widthImage * ( drawHeight / _heightImage );
					
					if( drawWidth + (_margin * 2) > _widthCanvas )
					{
						drawWidth = (float)_widthCanvas - (_margin * 2);
						drawHeight = _heightImage * ( ((float)drawWidth) / _widthImage );
					}
				}
			}			
			else if( paintWidth > _widthCanvas )
			{
				// Image wider
				drawWidth = (float)(_widthCanvas - (_margin * 2) );
				drawHeight = _heightImage * ( drawWidth / _widthImage );
			}
			else if( paintHeight > _heightCanvas )
			{
				// Image taller
				drawHeight = (float)( _heightCanvas - (_margin * 2) );
				drawWidth = _widthImage * ( drawHeight / _heightImage );
			}

			posX = ( ( _widthCanvas - drawWidth ) / 2 );
			posY = ( ( _heightCanvas - drawHeight ) / 2 );
		}

		boolean adjust = _adjusting;

		_adjusting = true;

		_widthDraw = (int)drawWidth;
		_heightDraw = (int)drawHeight;
		_posX = (int)posX;
		_posY = (int)posY;

		_adjusting = adjust;
	}
	
	@Override public boolean isDoubleBuffered() { return true; }

	/**
	 *  @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics graphic)
	{
		// TODO: All the paint control should be done by the PhotoCanvasControl and not here.
		//
		if( _screenBuffer != null )
		{
			Graphics screenBufferGraphic = _screenBuffer.getGraphics();

			//
			// Draw the stuff off screen
			//
			//super.paint( screenBufferGraphic );
			screenBufferGraphic.clearRect( 0, 0, _widthCanvas, _heightCanvas );

			if( !_adjusting ) // Avoid flickering on move
			{

				if( _image != null )
				{
					screenBufferGraphic.setColor( Color.white );
					screenBufferGraphic.drawRect(_posX - _border, _posY - _border, _widthDraw + _border * 2, _heightDraw + _border * 2);
					screenBufferGraphic.drawImage( _image, _posX, _posY, _widthDraw, _heightDraw, null );
				}
				else if( _noPhotos )
				{
					screenBufferGraphic.setColor( Color.white );
					screenBufferGraphic.drawString( "No PhotosFrom running.", 20, 20 );
				}
				else
				{
					screenBufferGraphic.setColor( Color.white );
					screenBufferGraphic.drawString( "Getting photos...", 20, 20 );
				}
			}

			if( _debug )
			{
				screenBufferGraphic.setXORMode( Color.red );
				screenBufferGraphic.drawString( _debugText, 20, 40 );
				screenBufferGraphic.drawString( "x:" + getWidth() + " y:" + getHeight(), 20, 60 );
			}

			//
			// Paint to screen
			//
			graphic.drawImage( _screenBuffer, 0, 0, this );
			screenBufferGraphic.dispose();
		}
	}

	//@Override
	public void componentHidden(ComponentEvent e) { }

	//@Override
	public void componentMoved(ComponentEvent e) { }

	//@Override
	public void componentResized(ComponentEvent e)
	{
		int currentWidth = this.getWidth();
		int currentHeight = this.getHeight();

		if( _widthCanvas != currentWidth || _heightCanvas != currentHeight )
		{
			_widthCanvas = currentWidth;
			_heightCanvas = currentHeight;

			createScreenBuffer();
			calculateSizeAndPosition();
		}
	}

	//@Override
	public void componentShown(ComponentEvent e)
	{
		createScreenBuffer();
	}

	private void createScreenBuffer()
	{
		if( _screenBuffer != null )
		{
			_screenBuffer.flush();
			_screenBuffer = null;
		}

		_screenBuffer = createImage( _widthCanvas,_heightCanvas );
	}
}