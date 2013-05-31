#!/bin/bash

# build using maven
echo "NCO: Maven instal Kernel"
mvn clean install assembly:single -e -X

# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
