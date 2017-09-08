#!/bin/bash
# download card images from NetrunnerDB

curl https://netrunnerdb.com/api/2.0/public/cards > cards.json
cards=( $(cat cards.json | jq -r '.data[].code' ) )
titles=( $(cat cards.json | jq -r '.data[].title' | tr -s ' ' | tr ' ' '-' | tr '[:upper:]' '[:lower:]' | sed "s/[^a-z0-9.-]//g") )
i=0;
for card in "${cards[@]}"
do
    title=${titles[$i]}
    if [ ! -f "../src/main/resources/static/img/cards/netrunner-$title.png" ]; then
        image=${imagesrc[$i]}
        echo "Downloading card image: $title"
        curl "https://netrunnerdb.com/card_image/$card.png" > "../src/main/resources/static/img/cards/netrunner-$title.png"
    fi
    ((i++))
done

# delete missing card stumps
find ../src/main/resources/static/img/cards/ -size -10k -delete
