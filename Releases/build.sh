#!/bin/bash

VERSION=0.4.2.54
JAR=/mnt/d/Mega/Projects/photoss/NetBeans/PhotoSS/out/artifacts/PhotoSS_jar/PhotoSS.jar

set -e

echo "Creating release files `date`"
echo "Version is $VERSION"

rm -rf general/
rm -rf linux/
rm -rf windows/
rm -f photoss-*-*.*

mkdir general windows linux

# Copy readme files
# cp ../ReadMes/README-General.txt general/README
# cp ../ReadMes/README-Linux.txt linux/README
# cp ../ReadMes/README-Windows.txt windows/README
cp ../ReadMes/* .

# Set version number into readme files.
sed "s/_VERSION_REPLACE_/${VERSION}/g" README-General.txt > general/README
sed "s/_VERSION_REPLACE_/${VERSION}/g" README-Linux.txt > linux/README
sed "s/_VERSION_REPLACE_/${VERSION}/g" README-Windows.txt > windows/README

rm -f README-*.txt

cp ${JAR} general/PhotoSS.jar
cp ${JAR} linux/PhotoSS.jar
cp ${JAR} windows/PhotoSS.jar

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

