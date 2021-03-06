--------------------------
**     PhotoSS  _VERSION_REPLACE_   **
--------------------------

Display all your photos.

Currently you can have photos from:
 - An account on Flickr
 - Folders on your computer.

-----------
* Running *
-----------

To run the program in a single window use:
java -jar PhotoSS.jar

You can control if further with some command line parameters.

To open 1 to 10 windows use this:
java -jar PhotoSS.jar window <number>

To run full screen like a screensaver use:
java -jar PhotoSS.jar fullscreen
or equivalently:
java -jar PhotoSS.jar screensaver


Screensaver Install
-------------------
You can use the idletime program to make PhotoSS run like a screensaver, like this:
./idletime 5 "java -jar ./PhotoSS.jar screensaver"
The 5 is the number of minutes of idletime detected before launching the screensaver.

--------------
* Properties *
--------------

This is an early version and currently has no setting dialog up. To adjust the settings open the text file in your user folder at /.akademy.co.uk/PhotoSS/PhotoSS.properties


Properties: flickr 
---------------------

To obtain photos from flickr you will have to enter the Flickr key, flickr secret and a user key into the properties. The properties are:
 - flickr.apiKey : key needed to download, available from Flickr.
 - flickr.apiSecret : secret needed to download, available from Flickr.
 - flickr.daysToReconnect : number of days to look for new photo downloads. Min 7 days.
 - flickr.photoCount : number of photos to download on next connection, maximum 500 photos.
 - flickr.userToken : a user token of the user the photos will come from.
 - flickr.photosets : (new) a semi-colon seperated list of flickr set names. Avoid spaces between sets.

Example settings:
flickr.apiKey= 9ab87c654ab32c109ab87c654ab32c109ab87c654ab32c109ab87c654ab32c10
flickr.apiSecret=1ab23c451ab23c45
flickr.daysToReconnect=7
flickr.photoCount=100
flickr.userToken=14280458@N05
flickr.photosets=My Favourites;Derbyshire;Another Set;North Wales 2009

Properties: folders
----------------------

You can open photos from various folderson your computer 

 - folder.folders : add folder paths seperated by a semi-colon. This will not search sub folders unless they are specified.

Example settings
folder.folders=/home/matthew;/home/matthew/Pictures

Properties: general
---------------------
Some general settings
 - general.photoShowTime : The number of milliseconds (1000th of a second) each photo will stay on screen for.
 - general.proxyHost : Set if you have a proxy, leave blank otherwise.
 - general.proxyPort : the port of your proxy if you have one.

Example settings
general.photoShowTime=10000
general.proxyHost=myproxy
general.proxyPort=80


----------------------------------
Copyright: (c) 2010. Akademy.co.uk
