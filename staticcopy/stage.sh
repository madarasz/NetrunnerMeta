#!/usr/bin/env bash
# get staging ready

start=$SECONDS

./getstatic.sh
./getcardstats.sh
./getcardimages.sh
rm -rf ../../NetrunnerMetaStatic/*
cp CNAME ../../NetrunnerMetaStatic/
cp ../src/main/resources/static/favicons/* ../../NetrunnerMetaStatic/
mv content/* ../../NetrunnerMetaStatic
cd ../../NetrunnerMetaStatic/
http-server &

test=$SECONDS

# nightwatch integration tests
cd ../NetrunnerMeta/staticcopy
nightwatch -c nightwatch/nightwatch.json --env phantomjs

end=$SECONDS
echo "Calculation:"
date -u -r $(( test - start )) +%T
echo "Tests:"
date -u -r $(( end - test )) +%T
echo "Total time:"
date -u -r $(( end - start )) +%T

# terminate http-server
read -p "... Press any key to terminate http server ..." -n 1 -s
ps aux | grep -i 'node /usr/local/bin/http-server' | awk '{print $2}' | xargs kill