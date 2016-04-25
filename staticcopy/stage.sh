#!/usr/bin/env bash
# get staging ready

./getstatic.sh
./getcardstats.sh
./getcardimages.sh
rm -rf ../../NetrunnerMetaStatic/*
cp CNAME ../../NetrunnerMetaStatic/
cp ../src/main/resources/static/favicons/* ../../NetrunnerMetaStatic/
mv content/* ../../NetrunnerMetaStatic
cd ../../NetrunnerMetaStatic/
http-server &

# nightwatch integration tests
cd ../NetrunnerMeta/staticcopy
nightwatch -c nightwatch/nightwatch.json

# terminate http-server
read -p "... Press any key to terminate http server ..." -n 1 -s
ps aux | grep -i 'node /usr/local/bin/http-server' | awk '{print $2}' | xargs kill