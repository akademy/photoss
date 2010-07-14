package uk.co.akademy.PhotoShow;

public class PropertyChanger {

	private PropertyChangerWindow _changer = null;
	
	public PropertyChanger( PropertyFetcher properties ) {
		
		_changer = new PropertyChangerWindow();
		
		/*_changer.setFolder_folders( properties.getProperty("folder.folders") );*/
		
		_changer.setVisible(true);
	}
}
