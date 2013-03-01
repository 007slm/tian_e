#!/bin/sh

# Clean up
svn revert src/com/orange/browser/dgil/DgilInstance.java
ant clean
rm *.apk

ant debug && mv bin/OrangeBrowser-debug.apk . && ant clean && sed 's/SetPointingAndroidMode/SetDynamicAndroidMode/g' src/com/orange/browser/dgil/DgilInstance.java > /tmp/tmp && cp /tmp/tmp src/com/orange/browser/dgil/DgilInstance.java && ant debug && mv bin/OrangeBrowser-debug.apk OrangeBrowser-debug-dynamic.apk

SVNREV=`LC_ALL=C svn info | awk '/Revision: / {print $2}'`
echo "Subversion revision: '"$SVNREV"'"

mv OrangeBrowser-debug.apk OrangeBrowser-debug-r$SVNREV.apk
mv OrangeBrowser-debug-dynamic.apk OrangeBrowser-debug-dynamic-r$SVNREV.apk

svn revert src/com/orange/browser/dgil/DgilInstance.java
ant clean
rm -Rf bin gen
