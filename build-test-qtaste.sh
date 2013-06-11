#! /bin/bash

export QTASTE_TRAVIS_CI="1"
# before_install:
sudo apt-get install xterm
export DISPLAY=:99.0
sh -e /etc/init.d/xvfb start
# run build script:
pushd qtaste-trunk
./buildAll.sh
popd
# execute qtaste demo aftersuccess:
pushd qtaste-trunk
./executeDemo.sh
popd
