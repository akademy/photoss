/**
 * 
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

/**
 * @author matthew
 *
 */
public class FullScreenShow {
	JFrame _frame;
	
	public FullScreenShow()
	{
		_frame = new JFrame("My Frame");
		
        _frame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        } ); 
        
		_frame.addKeyListener( new KeyListener()
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
        

	   _frame.setBackground(Color.black);
       _frame.setUndecorated(true); // Must come before you set anything else.
       _frame.setTitle("Photo Show");
       
       GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
       GraphicsDevice[] gs = ge.getScreenDevices();
       
       if( gs.length == 1 )
       {
	       PhotosFrom pf;
	       //pf = new PhotosFromTest();
	       pf = new PhotosFromFlickr();
	       
	       PhotoCanvas pc = new PhotoCanvas();
	       PhotoCanvasControl pcc = new PhotoCanvasControl( pc, pf );
	       
	       pc.setBackground(Color.black);
	       
	       int screenWidth = 0, screenHeight = 0;
	       	       
	       DisplayMode dm = gs[0].getDisplayMode();
	          
	       screenWidth = dm.getWidth();
	       screenHeight = dm.getHeight();
	       
	       gs[0].setFullScreenWindow(_frame);
	         

	       pc.setBounds(0, 0, screenWidth, screenHeight);
	       _frame.setSize(screenWidth, screenHeight);
	     
	       //_frame.setLayout( new SpringLayout() );
	       _frame.add( pc, SpringLayout.WEST );       
	       _frame.pack();
	
	       pcc.initilise();
       }
       else
       {
	       PhotosFrom pf, pf2;
	       pf = new PhotosFromTest();
	       pf2 = new PhotosFromTest();
	       //pf = new PhotosFromFolder();
	       //pf = new PhotosFromFlickr();
	       
	       PhotoCanvas pc = new PhotoCanvas();
	       PhotoCanvasControl pcc = new PhotoCanvasControl( pc, pf );
	       
	       PhotoCanvas pc2 = new PhotoCanvas();
	       PhotoCanvasControl pcc2 = new PhotoCanvasControl( pc2, pf2 );
	       
	       pc.setBackground(Color.black);
	       pc2.setBackground(Color.black);
	       
	       pc.setBounds(0, 0, 1280, 1024);
	       pc2.setBounds(0, 0, 1280, 1024);
	       
	       int screenWidth = 0, screenHeight = 0;
	       
	       //
	       // TODO: Need to figure out what to do on multiple screens...
	       //
	       
	       for (int i=0; i<gs.length; i++)
	       {
	           DisplayMode dm = gs[i].getDisplayMode();
	           
	           screenWidth += dm.getWidth();
	           if( dm.getHeight() > screenHeight )
	        	   screenHeight = dm.getHeight();
	       }
	       
	         for (int i=0; i<gs.length; i++) {
	        	 gs[i].setFullScreenWindow(_frame);
	         }
	         
	       _frame.setSize(screenWidth, screenHeight);
	     
	       //_frame.setLayout( new SpringLayout() );
	       _frame.add( pc, SpringLayout.WEST );
	       _frame.add( pc2, SpringLayout.EAST ); 
	       
	       _frame.pack();
	       
	       pcc.initilise();
	       pcc2.initilise();
       }
       
       //
       // Hide cursor (From http://sevensoft.livejournal.com/23460.html)
       // Set the mouse cursor to a transparent image.
       //
       Image transCursorImage;
       Cursor transCursor;
       String transCursorName = new String("transparentCursor");


		Toolkit toolkit = Toolkit.getDefaultToolkit();
		transCursorImage = toolkit.getImage("./1pxtrans_cursor.gif");
		MediaTracker mediaTracker = new MediaTracker(_frame);
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
		((Component)_frame).setCursor(transCursor);

       
       _frame.setVisible(true);
	}
}