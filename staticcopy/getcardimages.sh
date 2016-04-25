#!/bin/bash
# download card images from NetrunnerDB

cards=( $(curl https://netrunnerdb.com/api/cards/ | jq -r '.[].imagesrc' ) )
titles=( $(curl https://netrunnerdb.com/api/cards/ | jq -r '.[].title' | tr -s ' ' | tr ' ' '-' | tr '[:upper:]' '[:lower:]' | sed "s/[^a-z0-9.-]//g") )
i=0;
for card in "${cards[@]}"
do
    title=${titles[$i]}
    if [ ! -f "../src/main/resources/static/img/cards/netrunner-$title.png" ]; then
        echo "Downloading card image: $title"
        curl http://netrunnerdb.com/$card > "../src/main/resources/static/img/cards/netrunner-$title.png"
    fi
    ((i++))
done