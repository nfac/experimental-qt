#! /bin/bash

# execute Process
pushd demo
echo "Running All Linux Demo Campaign"
../bin/qtaste_campaign_start.sh ./TestCampaigns/AllLinuxDemo.xml || exit 1
popd
