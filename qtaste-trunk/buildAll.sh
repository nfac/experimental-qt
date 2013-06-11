#! /bin/bash
find . -name "*.sh" | xargs chmod +x

# deploy qtaste mvn dependencies
pushd dependencies
unzip qtaste_mvn_missing_dependencies.zip
cp -r ./javax ~/.m2/repository
cp -r ./jsyntaxpane/ ~/.m2/repository
rm -rf ./javax
rm -rf ./jsyntaxpane/
popd

# build the kernel
pushd kernel
./build.sh || exit 1
popd

# build other
if [ "$QTASTE_TRAVIS_CI" = "1" ]; then
  mvn install -Denvironment=travis || exit 1
else
  mvn install || exit 1
fi

# build plugins
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

# generate documentation
pushd doc
./generateDocs.sh || exit 1
popd
