#!/bin/bash
VERSION=0.4.0.42

echo "Creating release files `date`"
echo "Version is $VERSION"

rm -rf general/
rm -rf linux/
rm -rf windows/
rm -f photoss-*-*.*

mkdir general windows linux

# Copy readme files
cp ../ReadMes/README-General.txt general/README
cp ../ReadMes/README-Linux.txt linux/README
cp ../ReadMes/README-Windows.txt windows/README

#Set version number into readme files.
sed -i "s/_VERSION_REPLACE_/$VERSION/g" general/README
sed -i "s/_VERSION_REPLACE_/$VERSION/g" linux/README
sed -i "s/_VERSION_REPLACE_/$VERSION/g" windows/README

cp ../NetBeansWorkspace/dist/PhotoSS.jar general/
cp ../NetBeansWorkspace/dist/PhotoSS.jar linux/
cp ../NetBeansWorkspace/dist/PhotoSS.jar windows/

# Need to check if we have new launchers
cp ../Launchers/idletime linux/
cp ../Launchers/PhotoSS.scr windows/

# Create archives
cd linux
tar -czf "../photoss-$VERSION-linux.tar.gz" *

cd ../windows
zip -q "../photoss-$VERSION-windows.zip" *

cd ../general
zip -q "../photoss-$VERSION-general.zip" *

