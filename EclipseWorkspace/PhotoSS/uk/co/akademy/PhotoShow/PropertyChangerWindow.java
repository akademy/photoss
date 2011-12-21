package uk.co.akademy.PhotoShow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFileChooser;
import javax.swing.JButton;

public class PropertyChangerWindow extends JFrame {

	private static final long serialVersionUID = -3148895573914226334L;
	
	ArrayList<AbstractPhotosFromPanel> _photosFromPanels;
	
	public PropertyChangerWindow( ArrayList<AbstractPhotosFromPanel> photosFromPanels )
	{
		_photosFromPanels = photosFromPanels;
		
	    this.setSize(500, 400);
	    this.setTitle("PhotoSS Properties");
		
	    // Handle window closing events.
	    addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actionExit( false );
			}
	    } );
	    
	    JTabbedPane tabPane = new JTabbedPane();
		
		JButton okButton  = new JButton();
		JButton cancelButton = new JButton();

		okButton.setText("OK");
		cancelButton.setText("Cancel");

		okButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit( true );
			}
		});

		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit( false );
			}
		});

	    JPanel generalPanel = new JPanel();
	    
	    // TODO : General setting
	    generalPanel.add( new JLabel( "This is a work in progress, only folders can be changed." ), -1 );
	    generalPanel.add( new JLabel( "To change settings manually please edit the file at:"  ), -1 );
	    generalPanel.add( new JLabel( "YOURHOME/.akademy.co.uk/photoss/PhotoSS.properties"), -1 );
	    
	    
    	tabPane.addTab( "General", generalPanel );	
	    tabPane.setSelectedIndex(0);  
	    
	    for( AbstractPhotosFromPanel tab : photosFromPanels )
	    	tabPane.addTab( tab.getName(), tab );

	    this.add(tabPane);
		
	    this.add(cancelButton);
	    this.add(okButton);
		
		
	}
	
	public void initilise(PropertyFetcher properties)
	{
	    for( AbstractPhotosFromPanel tab : _photosFromPanels )
	    	tab.initialise( properties );
	}
	
	// Exit this program.
	private void actionExit( boolean commit )
	{
		if( commit )
		{
			for( AbstractPhotosFromPanel panel : _photosFromPanels )
				panel.updateProperties();

			Program.saveProperties();
		}
			
		System.exit(0);
	}
}
