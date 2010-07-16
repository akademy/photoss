/**
 * SHow the photos in fullscreen across mutliple screens
 */
package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

/**
 * @author matthew
 *
 */
public class FullScreenShow implements IShow
{
	ArrayList<JFrame> _screens = null;

	public FullScreenShow()
	{
		ArrayList<PhotoCanvas> photoCanvasList = new ArrayList<PhotoCanvas>();

		_screens = new ArrayList<JFrame>();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		int screens = gs.length;
		// screens = 1;
		
		for( int i = 0; i < screens; i++ )
		{
			JFrame frame = new JFrame("FullScreenView"+(i+1));
			
	        frame.addWindowListener( new WindowAdapter() {
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
						System.exit(0);
					}
				}
				public void keyTyped(KeyEvent event) {}
			} );
	        
			frame.setBackground(Color.black);
			frame.setUndecorated(true);

			DisplayMode dm = gs[i].getDisplayMode();
			frame.setSize(dm.getWidth(), dm.getHeight());
			
			PhotoCanvas pc = new PhotoCanvas( dm.getWidth(), dm.getHeight() );
			
			photoCanvasList.add( pc );

			frame.add( pc, SpringLayout.WEST );
			frame.pack();

			pc.setVisible(false);
			
			frame.setVisible(false);
			frame.setAlwaysOnTop(true); // Hard to debug!

			_screens.add( frame );
		}

	    
		//
		// Create a list of "PhotoFrom"
		//
		ArrayList<PhotosFrom> photosFromList = new ArrayList<PhotosFrom>();

		//photosFromList.add( new PhotosFromTest() );
		photosFromList.add( new PhotosFromFolder() );
		photosFromList.add( new PhotosFromFlickr() );


		//
		// Create the PhotoCanvasControl and start getting photos
		//
		PhotoCanvasControl pcc = new PhotoCanvasControl( photoCanvasList, photosFromList );

		//
		// Hide cursor (From http://sevensoft.livejournal.com/23460.html)
		// Set the mouse cursor to a transparent image.
		//
		String transCursorName = new String("transparentCursor");

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image transCursorImage = toolkit.getImage("./1pxtrans_cursor.gif");

		MediaTracker mediaTracker = new MediaTracker( _screens.get(0) );
		mediaTracker.addImage(transCursorImage, 0);
		try
		{
			mediaTracker.waitForID(0);
		}
		catch (InterruptedException ie)
		{
			System.err.println(ie);
			System.exit(1);
		}

		Cursor transCursor = toolkit.createCustomCursor(transCursorImage, new Point(0,0), transCursorName);

		for( JFrame frame : _screens )
			frame.setCursor( transCursor );
		
		for( JFrame frame : _screens )
			frame.setVisible(true);
		
		pcc.initilise();
	}

	@Override
	public boolean Initilise() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Run() {
		// TODO Auto-generated method stub
		return false;
	}
}