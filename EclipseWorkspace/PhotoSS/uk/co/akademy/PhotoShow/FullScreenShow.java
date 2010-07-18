/**
 * Show the photos in fullscreen across multiple screens
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
 * @author Matthew
 *
 */
public class FullScreenShow implements IShow
{
	JFrame _screen = null;
	int _screenNumber = 1;
	
	ArrayList<PhotoCanvas> _photoCanvasList = null;
	GraphicsDevice[] _graphicsDeviceArray = null;
	
	public FullScreenShow(int screenNumber)
	{
		_screenNumber = screenNumber;
	}	
	public FullScreenShow() {}


	public boolean initilise()
	{
		_photoCanvasList = new ArrayList<PhotoCanvas>();

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_graphicsDeviceArray = graphicsEnvironment.getScreenDevices();

		int screens = _graphicsDeviceArray.length;

		if( _screenNumber > screens )
			_screenNumber = 1;
		
		JFrame frame = new JFrame( "PhotoSS fullscreen-" + _screenNumber );
			
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        } ); 
        
		frame.addKeyListener( new KeyListener() 
		{
			public void keyPressed(KeyEvent event) {}
			public void keyReleased(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
				{
					System.exit(0);
				}
				
				// TODO: Add navigation keys so that you can move through
			}
			public void keyTyped(KeyEvent event) {}
		} );
        
		frame.setBackground(Color.black);
		frame.setUndecorated(true);

		DisplayMode dm = _graphicsDeviceArray[_screenNumber-1].getDisplayMode();
		frame.setSize(dm.getWidth(), dm.getHeight());
		
		PhotoCanvas pc = new PhotoCanvas( dm.getWidth(), dm.getHeight() );
		
		_photoCanvasList.add( pc );

		frame.add( pc, SpringLayout.WEST );
		frame.pack();

		pc.setVisible(false);
		
		frame.setVisible(false);
		frame.setAlwaysOnTop(true); // Hard to debug!

		_screen = frame;

		//
		// Hide cursor (From http://sevensoft.livejournal.com/23460.html)
		// Set the mouse cursor to a transparent image.
		//
		String transCursorName = new String("transparentCursor");

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image transCursorImage = toolkit.getImage("./1pxtrans_cursor.gif");

		MediaTracker mediaTracker = new MediaTracker( _screen );
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

		_screen.setCursor( transCursor );
		
		return true;
	}

	public boolean start( ArrayList<PhotosFrom> photosFromList )
	{
		if( photosFromList.size() > 0 )
		{
			PhotoCanvasControl pcc = new PhotoCanvasControl( _photoCanvasList, photosFromList );

			//_screen.setVisible(true);
			_graphicsDeviceArray[_screenNumber-1].setFullScreenWindow( _screen );
			
			pcc.initilise();
			
			return true;
		}
		
		return false;
	}
}