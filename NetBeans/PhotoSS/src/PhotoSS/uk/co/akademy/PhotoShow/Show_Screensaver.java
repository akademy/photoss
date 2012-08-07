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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

/**
 * @author Matthew
 *
 */
public class Show_Screensaver extends Show
{
	ArrayList<JFrame> _screens = null;
	
	GraphicsDevice[] _graphicsDeviceArray = null;
	int _screenNumber = 0;
	
	public Show_Screensaver() { }

	@Override
	public boolean initilise()
	{
		_screens = new ArrayList<JFrame>();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_graphicsDeviceArray = ge.getScreenDevices();

		_screenNumber = _graphicsDeviceArray.length;
		//_screenNumber = 1;
		
		for( int i = 0; i < _screenNumber; i++ )
		{
			JFrame frame = new JFrame("PhotoSS screensaver-"+(i+1));
			
			frame.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
				    System.exit(0);
				}
			} ); 
	        
			frame.addKeyListener( new KeyListener() {
				public void keyPressed(KeyEvent event) {}
				public void keyReleased(KeyEvent event) {
					System.exit(0);
				}
				public void keyTyped(KeyEvent event) {}
			} );
	        
			frame.addMouseListener( new MouseListener() {
				public void mouseClicked(MouseEvent event) {
					System.exit(0);
				}
				public void mouseEntered(MouseEvent event) {}
				public void mouseExited(MouseEvent event) {}
				public void mousePressed(MouseEvent event) {}
				public void mouseReleased(MouseEvent event) {}
			} );
			
			frame.addMouseMotionListener( new Show_Screensaver_MouseMotion() );
			
			frame.setBackground(Color.black);
			frame.setUndecorated(true);

			DisplayMode dm = _graphicsDeviceArray[i].getDisplayMode();
			frame.setSize(dm.getWidth(), dm.getHeight());
			
			PhotoCanvas pc = new PhotoCanvas( dm.getWidth(), dm.getHeight() );
			
			_photoCanvasList.add( pc );

			frame.add( pc, SpringLayout.WEST );
			frame.pack();
			
			pc.setVisible(false);

			frame.setVisible(false);
			frame.setAlwaysOnTop(true); // Hard to debug!

			_screens.add( frame );
		}

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
		
		return true;
	}

	@Override
	public void start( ArrayList<AbstractPhotosFrom> photosFromList )
	{
		for( int i=0; i<_screenNumber; i++ )
			_graphicsDeviceArray[i].setFullScreenWindow( _screens.get(i) );

		//_graphicsDeviceArray[_screenNumber-1].setFullScreenWindow( _screens.get(0) );

		super.start(photosFromList);
	}
}

class Show_Screensaver_MouseMotion implements MouseMotionListener {

	static final int mouseAllowed = 7;
	int mouseStartX = -1;
	int mouseStartY = -1;
	
	public void mouseDragged(MouseEvent me) {
	}

	public void mouseMoved(MouseEvent me) {
		int mouseCurrentX = (int) me.getPoint().getX();
		int mouseCurrentY = (int) me.getPoint().getY();

		if( mouseStartX == -1 && mouseStartY == -1 )
		{
			mouseStartX = mouseCurrentX;
			mouseStartY = mouseCurrentY;
		}
		else {
			if( mouseCurrentX > mouseStartX + mouseAllowed ||
					mouseCurrentX < mouseStartX - mouseAllowed ||
					mouseCurrentY > mouseStartY + mouseAllowed ||
					mouseCurrentY < mouseStartY - mouseAllowed ) {
				System.exit(0);
			}
		}
		
		
	}
	
}