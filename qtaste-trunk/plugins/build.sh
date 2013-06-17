#! /bin/bash

PLUGINS="tools javagui recorder"

for PLUGIN in $PLUGINS
do
    pushd $PLUGIN
    if [ "$QTASTE_HOSTED_CI" = "1" ]; then
      mvn clean install assembly:single -Denvironment=hosted_ci || exit 1
    else
      mvn clean install assembly:single || exit 1
    fi
    popd
done
