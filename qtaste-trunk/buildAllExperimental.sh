#! /bin/bash
find . -name "*.sh" | xargs chmod +x

# build other
mvn clean install -P qtaste-build-kernel-first -Denvironment=hosted_ci || exit 1

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


