#! /bin/bash

find . -name "*.sh" | xargs chmod +x

echo "NCO: deploy qtaste mvn dependencies"
pushd dependencies
unzip qtaste_mvn_missing_dependencies.zip
cp -r ./javax ~/.m2/repository
cp -r ./jsyntaxpane/ ~/.m2/repository
rm -rf ./javax
rm -rf ./jsyntaxpane/
popd
echo "NCO: end deploy dependencies"

echo "NCO: build the kernel"
pushd kernel
ls -l
./build.sh || exit 1
popd

echo "NCO: build other"
if [ "$TRAVIS_CI" = "1" ]; then
  mvn install -Denvironment=travis || exit 1
else
  mvn install || exit 1
fi

echo "NCO: build plugins"
pushd plugins_src
./build.sh || exit 1
popd

# build demonstrations
pushd demo
./build.sh || exit 1
popd

# create installer
pushd izpack
./createInstaller.sh || exit 1
popd
