/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author matthew
 *
 */
public class PhotoCanvas extends Canvas
{
	private static final long serialVersionUID = 8302867124480794953L;

	private enum Fading
	{
		None,
		Out,
		In
	}

	private Fading _fading;

	private String _debugText = ""; 
	private boolean _debug = false;
	
	private Photo _photoCurrent = null;
	private Photo _photoNext = null;

	private boolean _fadeOn = false;
	private int _fadeLength = 0;
	
	private int _width = 0,
				_height = 0;

	private float _opacity = 1.0f;
	private Timer _timerFade = null;

	private Image _screenBuffer = null;

	public PhotoCanvas()
	{
		this.setBackground(Color.black);
		this.setEnabled(false);
		
		_fading = Fading.None;
	}

	public void setDebug( boolean debug ) { _debug = debug; }
	public void setDebugText( String debugText ) { _debugText = debugText; }

	public void setFadeOn( boolean fade ) { _fadeOn = fade; }
	
	public void setNextPhoto( Photo photo ) { _photoNext = photo; }

	public void switchPhotoStart( int fadeLength )
	{
		_fadeLength = fadeLength;
		
		if( _fadeOn )
		{
			if( _photoCurrent != null )
			{
				startFade( Fading.Out );
			}
			else
			{
				// First time switch
				showNextPhoto();
				startFade( Fading.In );
			}
		}
		else
		{
			showNextPhoto();
			repaint();
		}
	}

	public void startFade( Fading fading )
	{
		_fading = fading;
		
		_timerFade = new Timer();
		_timerFade.schedule( new Fader( fading == Fading.Out ), 0, 500 );
	}

	public void endFade( boolean fadeOut )
	{
		if( _timerFade != null )
			_timerFade.cancel();

		if( fadeOut )
		{
			// Now fade in next one
			showNextPhoto();
			startFade( Fading.In );
		}
	}

	private void showNextPhoto()
	{
		Photo photoPrevious = _photoCurrent;
		
		_photoCurrent = _photoNext;
		
		if( photoPrevious != null && photoPrevious != _photoCurrent )
		{		
			// TODO: alert PhotoCanvasControl that we've done with this photo.
			// we should not be freeing things in PhotoCanvas... 
			photoPrevious.setImage( null );
		}
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
			// Just draw it from top left
			//
			// graphic.drawImage(_photoCurrent.getImage(), 0, 0, null);
			
			
			//
			// Fading
			//
			//Graphics2D g2d = (Graphics2D) screenBufferGraphic;
			//g2d.setComposite( makeComposite( _opacity ) );
			
			
			//
			// Centre image
			//
			int 	iPosX = ( _width - _photoCurrent.getWidth() ) / 2,
					iPosY = ( _height - _photoCurrent.getHeight() ) / 2;

			screenBufferGraphic.drawImage( _photoCurrent.getImage(), iPosX, iPosY, null );
		}
		else
		{
			screenBufferGraphic.setColor( Color.white );
			screenBufferGraphic.drawString( "Getting photos...", 20, 20 );
			//screenBufferGraphic.setColor( Color.black );
		}
		
		if( _debug )
		{
			screenBufferGraphic.setXORMode( Color.white );
			screenBufferGraphic.drawString( _debugText, 20, 40 );
			//screenBufferGraphic.setColor( Color.black );
		}
		
		//
		// Paint to on screen
		//
		graphic.drawImage( _screenBuffer, 0, 0, this );
		screenBufferGraphic.dispose();
	}
	
    /*
     * Set alpha composite.  For example, pass in 1.0f to have 100% opacity pass in 0.25f to have 25% opacity.
     */
    private AlphaComposite makeComposite( float alpha )
    {
        return (AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ));
    }
	
	/**
	 * 
	 */
	@Override
	public void update( Graphics g )
	{
		paint(g);
	}
	
	class Fader extends TimerTask
	{
		private boolean _fadingOut = false;
		private long _start = 0;
		
		public Fader( boolean fadingOut )
		{
			//_debugText = "";
			
			_start = System.currentTimeMillis();
			_fadingOut = fadingOut;
		}
		
		public void run()
		{
			float passed = System.currentTimeMillis() - _start;
			float newOpacity = ( passed / _fadeLength);
			
			if( _fadingOut )
			{
				newOpacity = 1.0f - newOpacity;
				if( newOpacity <= 0.0f )
					newOpacity = 0.0f;
			}
			else
			{
				if( newOpacity >= 1.0f )
					newOpacity = 1.0f;
			}
			
			
			_opacity = newOpacity;
			
			repaint();
			
			if( ( !_fadingOut && _opacity == 1.0f) || ( _fadingOut && _opacity == 0.0f) )
				endFade( _fadingOut );
		}
	}
}
