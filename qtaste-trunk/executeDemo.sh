#! /bin/bash

# execute Process
pushd demo
../bin/qtaste_start.sh -testsuite ./TestSuites/Process/ -testbed ./Testbeds/process.xml || exit 1
popd
