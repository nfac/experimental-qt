#! /bin/bash

echo "Executing script build-test-qtaste"

export QTASTE_TRAVIS_CI="1"
export DISPLAY=:99.0
# before_install:
#sudo apt-get install xterm
Xvfb :99.0 &
# run build script:
#pushd qtaste-trunk
#./buildAll.sh || exit 1
#popd
# execute qtaste demo aftersuccess:
pushd qtaste-trunk
./executeDemo.sh || exit 1
popd
