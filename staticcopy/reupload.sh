#!/usr/bin/env bash
# upload to github

cd ../../NetrunnerMetaStatic/
DATE=`date +%Y-%m-%d`
git add --all
git commit -m "update $DATE"
git push -u origin master
