package uk.co.akademy.PhotoShow;

import java.util.ArrayList;

public class PropertyChanger {

	private PropertyChangerWindow _changer = null;
	
	public PropertyChanger( PropertyFetcher properties ) {
		
		ArrayList<AbstractPhotosFromPanel> photosFromPanels = new ArrayList<AbstractPhotosFromPanel>();
		
		PhotosFromPanelFolder folder = new PhotosFromPanelFolder();
		
		photosFromPanels.add(folder);
		
		_changer = new PropertyChangerWindow( photosFromPanels );
		_changer.initilise(properties);
		
		_changer.setVisible(true);
		
		// TODO 
		for( AbstractPhotosFromPanel p : photosFromPanels )
			p.updateProperties();
		
	}
}
