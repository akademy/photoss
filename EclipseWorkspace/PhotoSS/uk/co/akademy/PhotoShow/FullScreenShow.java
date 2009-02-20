/**
 * 
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
public class FullScreenShow {
	ArrayList<JFrame> _frames = null;

	public FullScreenShow()
	{
	    ArrayList<PhotoCanvas> aPhotoCanvases = new ArrayList<PhotoCanvas>();
	    _frames = new ArrayList<JFrame>();
	     
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	        
	    for( int i=0;i<gs.length;i++ )
	    {
			JFrame frame = new JFrame("My Frame");
			
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
				}
			);
	        
	
		   frame.setBackground(Color.black);
	       frame.setUndecorated(true);
	       frame.setTitle("Photo Show" + i);
	       // _frame.setAlwaysOnTop(true); // Hard to debug!
	       
	       DisplayMode dm = gs[i].getDisplayMode();
	       frame.setSize(dm.getWidth(), dm.getHeight());
	       
    	   PhotoCanvas pc = new PhotoCanvas();
    	   
    	   pc.setBackground( Color.black );
	       pc.setBounds(0, 0, dm.getWidth(), dm.getHeight());
	       
	       aPhotoCanvases.add( pc );
	       
    	   frame.add( pc, SpringLayout.WEST );
	       frame.pack();
	       
	       frame.setAlwaysOnTop(true);
    	   
	       _frames.add( frame );
	    }
	    
	    // Loop around to blank screens
	    for( int i=0;i<gs.length;i++ )
	    {
	    	gs[i].setFullScreenWindow( _frames.get(i) );
	    }
       
	    PhotosFrom pf;
	    pf = new PhotosFromTest();
		//pf = new PhotosFromFolder();
		//pf = new PhotosFromFlickr();

		PhotoCanvasControl pcc = new PhotoCanvasControl( aPhotoCanvases, pf );
		pcc.initilise();
   
		//
		// Hide cursor (From http://sevensoft.livejournal.com/23460.html)
		// Set the mouse cursor to a transparent image.
		//
		Image transCursorImage;
		Cursor transCursor;
		String transCursorName = new String("transparentCursor");


		Toolkit toolkit = Toolkit.getDefaultToolkit();
		transCursorImage = toolkit.getImage("./1pxtrans_cursor.gif");
		
		MediaTracker mediaTracker = new MediaTracker( _frames.get(0) );
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

        transCursor = toolkit.createCustomCursor(transCursorImage, new Point(0,0), transCursorName);
        
        for( JFrame frame : _frames )
        	frame.setCursor(transCursor);
	}
}