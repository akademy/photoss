/**
 * 
 */
package uk.co.akademy.PhotoShow;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 * @author matthew
 *
 */
public class PhotoCanvas extends Canvas
{
	private Photo _photoCurrent = null;
	private Photo _photoNext = null;
	
	public PhotoCanvas()
	{
		this.setBackground(Color.black);
		this.setEnabled(false);
	}
	
	public void setNextPhoto(Photo photo )
	{
		_photoNext = photo;
	}
	
	public void switchPhoto()
	{
		_photoCurrent = _photoNext;
		
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics graphic)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		
		super.paint(graphic);
		
		graphic.setColor( Color.black );
		graphic.drawRect( 0, 0, width, height );
		
		if( _photoCurrent != null && _photoCurrent.isReady() )
		{
			//
			// Just draw it from top left
			//
			// graphic.drawImage(_photoCurrent.getImage(), 0, 0, null);

			//
			// Centre image
			//
			int 	iPosX = (width - _photoCurrent.getWidth() ) / 2,
					iPosY = (height - _photoCurrent.getHeight() ) / 2;
			
			graphic.drawImage( _photoCurrent.getImage(), iPosX, iPosY, null );
		}
		else
		{
			graphic.setColor( Color.white );
			graphic.drawString( "Getting photos...", 20, 20 );
			graphic.setColor( Color.black );
		}
	}
}
