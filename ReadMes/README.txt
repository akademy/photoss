---------------------
**     PhotoSS     **
---------------------

Display all your photos.

Currently you can have photos from:
 - An account on Flickr
 - Folders on your computer.

-----------
* Running *
-----------

To run in a couple of windows just run the app.
To run in full screen mode pass the command line "screensaver" to the jar file. All monitors will be used.

The command will likely be similar to :
java -jar PhotoSS.jar screensaver


Windows specific
----------------
To use as a screen saver on Windows drop both "PhotoSS.jar" and "PhotoSS.scr" files into your system folder.


--------------
* Properties *
--------------

This is an early version and currently has no setting dialog up. To adjust the settings open the text file in your user folder at /.akademy.co.uk/PhotoSS/PhotoSS.properties


Properties for flickr 
---------------------

To obtain photos from flickr you will have to enter the Flickr key, flickr secret and a user key into the properties. The properties are:
 - flickr.apiKey : key needed to download, available from Flickr
 - flickr.apiSecret : secret needed to download, available from Flickr.
 - flickr.daysToReconnect : number of days to look for new photo downloads. Min 7 days.
 - flickr.photoCount : number of photos to download on next connection
 - flickr.userToken : a user token for the user the photos will come from

Properties for folders
----------------------

You can open photos from various folderson your computer 

 - folder.folders : add folder paths seperated by a semi-colon. This will not search sub folders unless they are specified.

Properties for general settings
-------------------------------
Some general settings
 - general.photoShowTime : Set how many milli-seconds (1000 of a second) each photo will display for
 - general.proxyHost : Set if you have a proxy, leave blank otherwise.
 - general.proxyPort : the port of your proxy if you have one.



