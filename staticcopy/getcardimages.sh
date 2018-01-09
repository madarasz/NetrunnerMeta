#!/bin/bash
# download card images from NetrunnerDB

curl https://netrunnerdb.com/api/2.0/public/cards > cards.json
cards=( $(cat cards.json | jq -r '.data[].code' ) )
imageurls=( $(cat cards.json | jq -r '.data[].image_url' ) )
titles=( $(cat cards.json | jq -r '.data[].title' | tr -s ' ' | tr ' ' '-' | tr '[:upper:]' '[:lower:]' | sed "s/[^a-z0-9.-]//g") )
i=0;
for card in "${cards[@]}"
do
    title=${titles[$i]}
    if [ ! -f "../src/main/resources/static/img/cards/netrunner-$title.png" ]; then
        image=${imagesrc[$i]}
        imageurl=${imageurls[$i]}
        echo "Downloading card image: $title"
        if [ "$imageurl" == "null" ]; then
            curl "https://netrunnerdb.com/card_image/$card.png" > "../src/main/resources/static/img/cards/netrunner-$title.png"
        else
            curl $imageurl > "../src/main/resources/static/img/cards/netrunner-$title.png"
        fi
    fi
    ((i++))
done

# delete missing card stumps
find ../src/main/resources/static/img/cards/ -size -10k -delete
