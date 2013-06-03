#!/bin/bash

#remove previous python compilation classes.
pushd ../tools/jython/lib/Lib/
rm -f *.class
popd

# build using maven
if [ "$TRAVIS_CI" = "1" ]; then
mvn clean install assembly:single -Denvironment=travis
else
mvn clean install assembly:single
fi


# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
