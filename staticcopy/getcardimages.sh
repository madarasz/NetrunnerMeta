#!/bin/bash

cards=( $(curl http://netrunnerdb.com/api/cards/ | jq -r '.[].imagesrc' ) )
for card in "${cards[@]}"
do
    if [ ! -f "../src/main/resources/static/img/cards/${card:42}" ]; then
        echo ${card:42}
        curl http://netrunnerdb.com/$card > "../src/main/resources/static/img/cards/${card:42}"
    fi
done