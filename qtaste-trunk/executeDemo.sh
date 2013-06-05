#! /bin/bash

# execute Process
pushd demo

#echo "Running Playback Test Suite"
#../bin/qtaste_start.sh -testsuite ./TestSuites/PlayBack/ -testbed ./Testbeds/playback.xml
#echo "Running Process Test Suite"
#../bin/qtaste_start.sh -testsuite ./TestSuites/Process/ -testbed ./Testbeds/process.xml
#echo "Running Translate Test Suite"
#../bin/qtaste_start.sh -testsuite ./TestSuites/TestTranslate/ -testbed ./Testbeds/demo_web.xml

echo "Running All Linux Demo Campaign"
../bin/qtaste_campaign_start.sh ./TestCampaigns/AllLinuxDemo.xml || exit 1

popd
