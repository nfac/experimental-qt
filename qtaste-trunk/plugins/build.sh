#! /bin/bash

PLUGINS="tools javagui recorder"

for PLUGIN in $PLUGINS
do
    pushd $PLUGIN
    if [ "$TRAVIS_CI" = "1" ]; then
      mvn clean install assembly:single -Denvironment=travis || exit 1
    else
      mvn clean install assembly:single || exit 1
    fi
    popd
done
