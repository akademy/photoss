package uk.co.akademy.PhotoShow;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class PropertyChangerWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3148895573914226334L;

	private String general_proxyHost= "";
	private int general_photoShowTime = 10000;
	private int general_proxyPort = 8080;
	private String flickr_lastConnection= "";
	private String flickr_userToken="";
	private String flickr_apiSecret= "";
	private String flickr_apiKey = "";
	private String flickr_photosets = "";
	private int flickr_photoCount = 25;
	private int flickr_daysToReconnect = 7;
	private String folder_folders = "";
	
	public PropertyChangerWindow()
	{
	    this.setTitle("Property Changer");
		this.setSize( 600, 100 );
		
	    // Handle window closing events.
	    addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        actionExit();
	      }
	    } );

	    this.add( new JLabel( "Please edit the file at HOME/.akademy.co.uk/photoss/PhotoSS.properties"), -1 );
	}
	
	// Exit this program.
	private void actionExit()
	{
	  System.exit(0);
	}

	
	@Override
	public void setVisible(boolean b) {
		// TODO set up window with property settings
		super.setVisible(b);
	}



	public int getFlickr_daysToReconnect() {
		return flickr_daysToReconnect;
	}

	public void setFlickr_daysToReconnect(int flickrDaysToReconnect) {
		flickr_daysToReconnect = flickrDaysToReconnect;
	}

	public String getGeneral_proxyHost() {
		return general_proxyHost;
	}

	public void setGeneral_proxyHost(String generalProxyHost) {
		general_proxyHost = generalProxyHost;
	}

	public int getFlickr_photoCount() {
		return flickr_photoCount;
	}

	public void setFlickr_photoCount(int flickrPhotoCount) {
		flickr_photoCount = flickrPhotoCount;
	}

	public String getFlickr_apiKey() {
		return flickr_apiKey;
	}

	public void setFlickr_apiKey(String flickrApiKey) {
		flickr_apiKey = flickrApiKey;
	}

	public int getGeneral_proxyPort() {
		return general_proxyPort;
	}

	public void setGeneral_proxyPort(int generalProxyPort) {
		general_proxyPort = generalProxyPort;
	}

	public String getFlickr_lastConnection() {
		return flickr_lastConnection;
	}

	public void setFlickr_lastConnection(String flickrLastConnection) {
		flickr_lastConnection = flickrLastConnection;
	}

	public String getFlickr_userToken() {
		return flickr_userToken;
	}

	public void setFlickr_userToken(String flickrUserToken) {
		flickr_userToken = flickrUserToken;
	}

	public String getFolder_folders() {
		return folder_folders;
	}

	public void setFolder_folders(String folderFolders) {
		folder_folders = folderFolders;
	}

	public String getFlickr_apiSecret() {
		return flickr_apiSecret;
	}

	public void setFlickr_apiSecret(String flickrApiSecret) {
		flickr_apiSecret = flickrApiSecret;
	}

	public void setFlickr_photosets(String flickr_photosets) {
		this.flickr_photosets = flickr_photosets;
	}
	
	public String getFlickr_photosets() {
		return flickr_photosets;
	}

	public int getGeneral_photoShowTime() {
		return general_photoShowTime;
	}
	
	public void setGeneral_photoShowTime(int generalPhotoShowTime) {
		general_photoShowTime = generalPhotoShowTime;
	}
	
	
}
