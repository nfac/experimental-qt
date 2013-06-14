#! /bin/bash

echo "Executing script build-test-qtaste"

export QTASTE_TRAVIS_CI="1"
export DISPLAY=:99.0

# Before build configurations:
find . -name "*.sh" | xargs chmod +x
Xvfb :99.0 &

# Build:
pushd qtaste-trunk
./buildAll.sh || exit 1
popd

# Execute qtaste demo after build success:
pushd qtaste-trunk
./executeDemo.sh || exit 1
popd
