/*
 * Display photos in one or more windows.
 */
package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Show_Window extends Show
{
	ArrayList<JFrame> _windows = null;
	
	int _width = 600;
	int _height = 400;
	int _windowNumber = 1;
	
	
	public Show_Window( int windowNumber, int width, int height )
	{
		_windowNumber = windowNumber;
		_width = width;
		_height = height;
	}	
	public Show_Window( int windowNumber )
	{
		_windowNumber = windowNumber;
	}
	public Show_Window() {}

	@Override
	public boolean initilise() 
	{
		_windows = new ArrayList<JFrame>(_windowNumber);
		
		for( int i = 0; i<_windowNumber; i++ )
		{
			JFrame frame = new JFrame( "PhotoSS window-" + (i+1) );
			
			frame.addWindowListener( new WindowAdapter() {
			    @Override
			    public void windowClosing(WindowEvent e) {
				 System.exit(0);
			    }
			} ); 
	
			frame.addKeyListener( new KeyListener() 
			{
				public void keyPressed(KeyEvent event) {}
				public void keyReleased(KeyEvent event) {
					//if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
					{
						//System.exit(0);
					}
				}
				public void keyTyped(KeyEvent event) {}
			} );
	
			frame.setBackground(Color.black);
			
			Point loca = frame.getLocation();
			loca.translate( i*50, i*20 );
			frame.setLocation( loca );

			PhotoCanvas pc = new PhotoCanvas( _width, _height );
			
			_photoCanvasList.add( pc );
	
			frame.add( pc );
			frame.pack();
			
			pc.setVisible(false);
			
			_windows.add( frame );
		}
		
		return true;
	}

	public void start()
	{		
		for( JFrame frame : _windows )
			frame.setVisible(true);
	}
}
