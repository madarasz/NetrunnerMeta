#!/usr/bin/env bash
# get staging ready

./getstatic.sh
rm -rf ../../NetrunnerMetaStatic/*
cp CNAME ../../NetrunnerMetaStatic/
cp ../src/main/resources/static/favicons/* ../../NetrunnerMetaStatic/
mv content/* ../../NetrunnerMetaStatic
cd ../../NetrunnerMetaStatic/
http-server