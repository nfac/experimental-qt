#!/bin/bash

#remove previous python compilation classes.
pushd ../tools/jython/lib/Lib/
rm -f *.class
popd

# build using maven
if [ "$QTASTE_TRAVIS_CI" = "1" ]; then
  mvn -X clean install assembly:single -Denvironment=travis || exit 1
else
  mvn clean install assembly:single || exit 1
fi


# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
