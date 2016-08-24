#!/bin/bash
# get card statistics

cd content
mkdir Cards
mkdir Cards/24  #exception for 24/7 card
mkdir JSON
mkdir JSON/Cards
mkdir JSON/Cards/24

titles=( $(curl https://netrunnerdb.com/api/2.0/public/cards | jq -r '.data[].title' | sed 's/ /%20/g') )
for title in "${titles[@]}"
do
  mkdir "Cards/${title//%20/ }"
  mkdir "JSON/Cards/${title//%20/ }"
  echo "Calculating card: $title"
  curl http://localhost:8080/Cards/$title/ > "Cards/${title//%20/ }/index.html"
  curl http://localhost:8080/JSON/Cards/$title/card.json > "JSON/Cards/${title//%20/ }/card.json"
done