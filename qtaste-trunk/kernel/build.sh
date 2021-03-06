#!/bin/bash

#remove previous python compilation classes.
pushd ../tools/jython/lib/Lib/
rm -f *.class
popd

# build using maven
if [ "$QTASTE_HOSTED_CI" = "1" ]; then
  mvn clean install assembly:single -Denvironment=hosted_ci || exit 1
else
  mvn clean install assembly:single || exit 1
fi

# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
