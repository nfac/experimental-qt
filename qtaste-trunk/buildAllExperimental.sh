#! /bin/bash
find . -name "*.sh" | xargs chmod +x

# build the kernel
# pushd kernel
# mvn install assembly:single -Denvironment=hosted_ci  || exit 1
# popd

# build other
if [ "$QTASTE_HOSTED_CI" = "1" ]; then
  mvn clean install -P BuildKernelFirst -Denvironment=hosted_ci || exit 1
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


