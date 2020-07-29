/**
 * Show the photos in fullscreen across multiple screens
 */
package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.Component;
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

		// Get screen DPI scaled value (assumed same on each screen)
		GraphicsConfiguration defaultConfiguration = ge.getDefaultScreenDevice().getDefaultConfiguration();
		double xScale = defaultConfiguration.getDefaultTransform().getScaleX();
		double yScale = defaultConfiguration.getDefaultTransform().getScaleY();
		
		_screenNumber = _graphicsDeviceArray.length;
		//_screenNumber = 1;
		
		for( int i = 0; i < _screenNumber; i++ )
		{
			JFrame frame = new JFrame("PhotoSS screensaver-"+(i+1));
			//Component glassPane = frame.getGlassPane();

			frame.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
				    System.exit(0);
				}
			} ); 
	        
			frame.addKeyListener( new Show_Screensaver_Key() );
			frame.addMouseListener( new Show_Screensaver_Mouse() );
			frame.addMouseMotionListener( new Show_Screensaver_MouseMotion() );
			
			frame.setBackground(Color.black);
			frame.setUndecorated(true);

			_graphicsDeviceArray[i].setFullScreenWindow( frame );
			DisplayMode dm = _graphicsDeviceArray[i].getDisplayMode();
			int width = dm.getWidth(),
					height = dm.getHeight();
			
			frame.setSize(width, height);
			
			PhotoCanvas pc = new PhotoCanvas( width, height, xScale, yScale );
			
            pc.addMouseMotionListener(new Show_Screensaver_MouseMotion() );
			pc.addKeyListener( new Show_Screensaver_Key() );
			pc.addMouseListener( new Show_Screensaver_Mouse() );
                        
			_photoCanvasList.add( pc );

			frame.add( pc, SpringLayout.WEST );
			frame.pack();
			
			pc.setVisible(true);

			frame.setVisible(true);
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

	public void start(  )
	{

	}
}


class Show_Screensaver_Key implements KeyListener {
    public void keyPressed(KeyEvent event) {}
    public void keyReleased(KeyEvent event) {
	System.exit(0);
    }
    public void keyTyped(KeyEvent event) {}
}

class Show_Screensaver_Mouse implements MouseListener {

    public void mouseClicked(MouseEvent event) {
	System.exit(0);
    }
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}
    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
}

class Show_Screensaver_MouseMotion implements MouseMotionListener {

    static final int mouseAllowed = 7;
    int mouseStartX = -1;
    int mouseStartY = -1;

    public void mouseDragged(MouseEvent me) {}

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