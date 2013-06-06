#! /bin/bash

PLUGINS="tools javagui recorder"

targetDir=../plugins/SUT
if [ ! -d "$targetDir" ]
then
    mkdir $targetDir
fi

for PLUGIN in $PLUGINS
do
    pushd $PLUGIN
    if [ "$QTASTE_TRAVIS_CI" = "1" ]; then
      mvn clean install assembly:single -Denvironment=travis || exit 1
    else
      mvn clean install assembly:single || exit 1
    fi
    cp target/*-deploy.jar ../$targetDir
    popd
done

PLUGINS="AddonDemo ControlScriptBuilderAddOn"

targetDir=../plugins/

for PLUGIN in $PLUGINS
do
    pushd $PLUGIN
    if [ "$QTASTE_TRAVIS_CI" = "1" ]; then
      mvn clean install assembly:single -Denvironment=travis || exit 1
    else
      mvn clean install assembly:single || exit 1
    fi
    cp target/*-deploy.jar ../$targetDir
    popd
done
