#!/usr/bin/env bash

./getstatic.sh
rm -rf ../../NetrunnerMetaStatic/*
cp CNAME ../../NetrunnerMetaStatic/
mv content/* ../../NetrunnerMetaStatic
cd ../../NetrunnerMetaStatic/

DATE=`date +%Y-%m-%d`
git add --all
git commit -m "update $DATE"
git push -u origin master
