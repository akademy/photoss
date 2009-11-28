/*
 * DIsplay photos in one of more windows.
 */
package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

public class WindowShow implements IShow
{
	ArrayList<JFrame> _windows = null;
	
	int _width = 600;
	int _height = 400;
	
	public WindowShow( int windowNumber )
	{
		_windows = new ArrayList<JFrame>(windowNumber);
		
		ArrayList<PhotoCanvas> photoCanvasList = new ArrayList<PhotoCanvas>(windowNumber);
		
		for( int i = 0; i<windowNumber; i++ )
		{
			JFrame frame = new JFrame( "PhotoSS " + (i+1) );
			
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
			//frame.setSize( _width, _height );
	
			PhotoCanvas pc = new PhotoCanvas( _width, _height );
			//pc.setBounds(0, 0, _width, _height);
			
			photoCanvasList.add( pc );
	
			frame.add( pc );
			frame.pack();
			
			pc.setVisible(false);
			
			_windows.add( frame );
		}
		
		ArrayList<PhotosFrom> photosFromList = new ArrayList<PhotosFrom>();

		//photosFromList.add( new PhotosFromTest() );
		photosFromList.add( new PhotosFromFolder() );
		//photosFromList.add( new PhotosFromFlickr() );
		
		PhotoCanvasControl pcc = new PhotoCanvasControl( photoCanvasList, photosFromList );
		
		for( JFrame frame : _windows )
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
