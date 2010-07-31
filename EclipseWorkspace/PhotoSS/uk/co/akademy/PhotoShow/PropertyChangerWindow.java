package uk.co.akademy.PhotoShow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JFileChooser;

public class PropertyChangerWindow extends JFrame {

	private static final long serialVersionUID = -3148895573914226334L;

	/*private String general_proxyHost= "";
	private int general_photoShowTime = 10000;
	private int general_proxyPort = 8080;*/
	
	ArrayList<AbstractPhotosFromPanel> _photosFromPanels;
	
	public PropertyChangerWindow( ArrayList<AbstractPhotosFromPanel> photosFromPanels )
	{
		_photosFromPanels = photosFromPanels;
		
	    this.setSize(500, 300);
	    this.setTitle("PhotoSS Properties");
		
	    // Handle window closing events.
	    addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        actionExit();
	      }
	    } );
	    
	    JTabbedPane tabPane = new JTabbedPane();
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
	}
	
	public void initilise(PropertyFetcher properties)
	{

	    for( AbstractPhotosFromPanel tab : _photosFromPanels )
	    	tab.initilise( properties );	
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if( e.getActionCommand().equals("ChooseFolder") ) 
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.showDialog(/*panelFromFolders*/this, "Select folder");
		}
	}
	
	// Exit this program.
	private void actionExit()
	{
		for( AbstractPhotosFromPanel panel : _photosFromPanels )
			panel.updateProperties();
		
		Program.saveProperties();
			
		System.exit(0);
	}

	
	@Override
	public void setVisible(boolean b) {
		// TODO set up window with property settings
		super.setVisible(b);
	}
}
