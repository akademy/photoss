package uk.co.akademy.PhotoShow;

import javax.swing.JPanel;

@SuppressWarnings("serial") // Just a bit silly in an abstract class...
public abstract class AbstractPhotosFromPanel extends JPanel {

	/*private static final long serialVersionUID = 1L;*/

	abstract boolean initilise( PropertyFetcher properties );
	abstract void updateProperties();
}
