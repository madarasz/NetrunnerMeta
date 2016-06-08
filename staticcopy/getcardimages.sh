#!/bin/bash
# download card images from NetrunnerDB

curl https://netrunnerdb.com/api/cards/ > cards.json
cards=( $(cat cards.json | jq -r '.[].imagesrc' ) )
imagesrc=( $(cat cards.json | jq -r '.[].imagesrc' ) )
titles=( $(cat cards.json | jq -r '.[].title' | tr -s ' ' | tr ' ' '-' | tr '[:upper:]' '[:lower:]' | sed "s/[^a-z0-9.-]//g") )
i=0;
for card in "${cards[@]}"
do
    title=${titles[$i]}
    if [ ! -f "../src/main/resources/static/img/cards/netrunner-$title.png" ]; then
        image=${imagesrc[$i]}
        echo "Downloading card image: $title"
        curl "https://netrunnerdb.com$image" > "../src/main/resources/static/img/cards/netrunner-$title.png"
    fi
    ((i++))
done