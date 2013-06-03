#! /bin/bash

echo "NCO: deploy qtaste mvn dependencies"
pushd dependencies
unzip qtaste_mvn_missing_dependencies.zip
cp -r ./javax ~/.m2/repository
cp -r ./jsyntaxpane/ ~/.m2/repository
rm -rf ./javax
rm -rf ./jsyntaxpane/
popd
echo "NCO/ end deploy dependencies"


echo "NCO"
ls -l ~/.m2/repository

echo "NCO: build the kernel"
pushd kernel
ls -l
./build.sh || exit 1
popd

# build other
mvn install || exit 1
