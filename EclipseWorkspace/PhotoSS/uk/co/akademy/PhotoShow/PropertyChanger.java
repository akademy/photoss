package uk.co.akademy.PhotoShow;

import java.util.ArrayList;

public class PropertyChanger {

	private PropertyChangerFrame _changer = null;
	
	public PropertyChanger( PropertyFetcher properties ) {
		
		ArrayList<AbstractPhotosFromPanel> photosFromPanels = new ArrayList<AbstractPhotosFromPanel>();
		
		photosFromPanels.add( new PhotosFromPanelFolder() );
		photosFromPanels.add( new PhotosFromPanel_Flickr() );

		_changer = new PropertyChangerFrame( photosFromPanels );
		_changer.initialise(properties);
		
		_changer.setVisible(true);
	}
}
