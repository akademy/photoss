/**
 * Show the photos in fullscreen across multiple screens
 */
package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
public class Show_FullScreen implements Show
{
	JFrame _screen = null;
	int _screenNumber = 1;
	
	ArrayList<PhotoCanvas> _photoCanvasList = null;
	GraphicsDevice[] _graphicsDeviceArray = null;
	
	public Show_FullScreen(int screenNumber)
	{
		_screenNumber = screenNumber;
	}	
	public Show_FullScreen() { }


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
				
				// TODO: Add navigation keys so that you can move through the photos
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
		
		return true;
	}

	public void start( ArrayList<AbstractPhotosFrom> photosFromList )
	{
		PhotoCanvasControl pcc = new PhotoCanvasControl( _photoCanvasList, photosFromList );
		
		_graphicsDeviceArray[_screenNumber-1].setFullScreenWindow( _screen );
		
		pcc.initialise();
		pcc.start();
	}
}