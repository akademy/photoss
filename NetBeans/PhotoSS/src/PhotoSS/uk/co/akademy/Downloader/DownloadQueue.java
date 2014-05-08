/*
 * One place to download all the files. This lets us control the number of simulataneous downloads
 */

package uk.co.akademy.Downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author sers0034
 */
public class DownloadQueue extends Observable implements Observer, Runnable {
	
	private ArrayList<Download> _downloadQueue;
	private ArrayList<Download> _downloadingList;
	private boolean _downloadRunning = false;
	private int _maxSimultaneousDownloads = 4;
	private String _tempFolder;
	
	public DownloadQueue( String tempFolder ) {
		
		_downloadQueue = new ArrayList<Download>();
		_downloadingList = new ArrayList<Download>();
		
		_tempFolder = tempFolder;
	}
	
	public void setMaxSimultaneousDownloads( int msd ) {
		_maxSimultaneousDownloads = msd;
	}
	
	public void addDownload( Download download, String groupName ) {
		
		download.setDownloadFolder( _tempFolder + File.separator + groupName );
		_downloadQueue.add( download );
	}
	
	public void run() {
		
		if( !_downloadRunning ) {
			
			_downloadRunning = true;

			while( _downloadRunning ) {

				if( !_downloadQueue.isEmpty() ) {

					while( _downloadingList.size() < _maxSimultaneousDownloads ) {

						Download d = _downloadQueue.remove(0); // pop
						_downloadingList.add( d );
						
						d.addObserver( this );
						d.download();
					}
				}

				try {
					Thread.sleep( 50 );
					
				} catch (InterruptedException e) { }
			}
		}
	}

	public void completeDownloading() { _downloadRunning = false; }
	public void cancelDownloading() {
		
		_downloadRunning = false;
		for( Download d : _downloadQueue ) {
			d.cancel();
		}
	}
	
	public void update( Observable o, Object arg )
	{
		//Download download = _downloadQueue.get( _downloadQueue.indexOf(o) );
		Download download = _downloadingList.get( _downloadingList.indexOf(o) );
			 
		switch( download.getStatus() )
		{
			case Download.DOWNLOADING:
			//case Download.PAUSED:
				break;
				
			case Download.CANCELLED:
			case Download.ERROR:
			{
				File file = new File( download.getDownloadedFilePosition() );
				if( file.exists() )
					file.delete();
			}
			break;
				
			case Download.COMPLETE:
			{
				download.deleteObservers();
				_downloadingList.remove(download);
				
				File downloaded = new File( download.getDownloadedFilePosition() );
				File stored = new File( download.getStoreFilePosition() );

				try {
					customBufferStreamCopy(downloaded, stored);
					downloaded.delete();
				} catch (IOException ex) {
					Logger.getLogger(DownloadQueue.class.getName()).log(Level.SEVERE, null, ex);
				}
				
				setChanged();
				notifyObservers( new uk.co.akademy.PhotoShow.Photo( stored ) );
			}
		}
	}
	
	// http://www.baptiste-wicht.com/2010/08/file-copy-in-java-benchmark/5/
	private static final int BUFFER = 8192;
	private void customBufferStreamCopy( File from, File to) throws IOException {
		InputStream fis = null;
		OutputStream fos = null;
		
		try {
		     fis = new FileInputStream(from);
		     fos = new FileOutputStream(to);

		     byte[] buf = new byte[BUFFER];

		     int i;
		     while ((i = fis.read(buf)) != -1) {
			  fos.write(buf, 0, i);
		     }
		}
		catch (IOException e) {
		     //e.printStackTrace();
		     throw e;
		} 
		finally {
			
			try {
				if( fis != null ) {
					fis.close();
				}
			} 
			catch (IOException e) {
			  //e.printStackTrace();
			} 
			finally {
				
				try {
					if( fos != null ) {
						fos.close();
					}
				}
				catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}
	
	
}
