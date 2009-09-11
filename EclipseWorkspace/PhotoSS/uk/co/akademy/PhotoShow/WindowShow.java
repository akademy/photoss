package uk.co.akademy.PhotoShow;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

public class WindowShow implements IShow
{
	ArrayList<PhotoCanvas> photoCanvasList = null;
	JFrame frame = null;
	
	int width = 500;
	int height = 300;
	
	public WindowShow()
	{
		photoCanvasList = new ArrayList<PhotoCanvas>();
		
		frame = new JFrame("PhotoSS");
		
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
		frame.setSize(width, height);

		PhotoCanvas pc = new PhotoCanvas();

		pc.setBackground( Color.black );
		pc.setBounds(0, 0, width, height);

		photoCanvasList.add( pc );

		frame.add( pc );
		frame.pack();
		
		ArrayList<PhotosFrom> photosFromList = new ArrayList<PhotosFrom>();

		//photosFromList.add( new PhotosFromTest() );
		photosFromList.add( new PhotosFromFolder() );
		photosFromList.add( new PhotosFromFlickr() );
		
		PhotoCanvasControl pcc = new PhotoCanvasControl( photoCanvasList, photosFromList );
		pcc.initilise();
		
		frame.setVisible(true);
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
