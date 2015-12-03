#!/bin/bash
#set -x
# brew install jq

rm -rf content
mkdir content
cd content

curl http://localhost:8080/ > index.html

# create directories
dirs=( "static" "static/css" "static/js" "static/js/vendor" "JSON" "DataTable" "DataTable/Cardpool"
"DPStats"
"DataTable/DPStats"
"DataTable/DPStats/Top"
"DataTable/DPStats/Top/runner"
"DataTable/DPStats/Top/corp"
"DataTable/DPStats/Top/runner/faction"
"DataTable/DPStats/Top/corp/faction"
"DataTable/DPStats/Top/runner/identity"
"DataTable/DPStats/Top/corp/identity"
"DataTable/DPStats/All"
"DataTable/DPStats/All/runner"
"DataTable/DPStats/All/corp"
"DataTable/DPStats/All/runner/faction"
"DataTable/DPStats/All/corp/faction"
"DataTable/DPStats/All/runner/identity"
"DataTable/DPStats/All/corp/identity"
"DataTable/DPStats/Compare"
"DataTable/DPStats/Compare/runner"
"DataTable/DPStats/Compare/corp"
"DataTable/DPStats/Compare/runner/faction"
"DataTable/DPStats/Compare/corp/faction"
"DataTable/DPStats/Compare/runner/identity"
"DataTable/DPStats/Compare/corp/identity"
"DataTable/MDSIdentity"
"JSON/Cards"
"JSON/Cards/Cardpack"
"JSON/Cards/Cardpool"
"JSON/Cards/Cardpack/runner"
"JSON/Cards/Cardpack/corp"
"JSON/Cards/Cardpool/runner"
"JSON/Cards/Cardpool/corp"
"JSON/DPStats"
"JSON/DPStats/Identities"
"JSON/DPStats/Identities/runner"
"JSON/DPStats/Identities/corp"
"JSON/Deck"
"JSON/Average"
"MDSIdentity"
)
for dir in "${dirs[@]}"
do
    mkdir $dir
done

# download static files
files=( "static/css/bootstrap.min.css"
"static/css/bootstrap-theme.min.css"
"static/css/main.css"
"static/js/jquery-1.11.3.min.js"
"static/js/vendor/bootstrap.min.js"
"static/js/main.js"
"JSON/Cardpool"
"DataTable/Cardpool/runner"
"DataTable/Cardpool/corp" )

for file in "${files[@]}"
do
    touch $file
    curl http://localhost:8080/$file > $file
done

packs=(
"Data%20and%20Destiny"
"The%20Universe%20of%20Tomorrow"
"Old%20Hollywood"
"The%20Underway"
"Chrome%20City"
"Breaker%20Bay"
"The%20Valley"
)

# for each pack
for pack in "${packs[@]}"
do
    packfiles=(
    "DataTable/DPStats/Top/runner/faction/$pack"
    "DataTable/DPStats/Top/corp/faction/$pack"
    "DataTable/DPStats/Top/runner/identity/$pack"
    "DataTable/DPStats/Top/corp/identity/$pack"
    "DataTable/DPStats/All/runner/faction/$pack"
    "DataTable/DPStats/All/corp/faction/$pack"
    "DataTable/DPStats/All/runner/identity/$pack"
    "DataTable/DPStats/All/corp/identity/$pack"
    "DataTable/DPStats/Compare/runner/faction/$pack"
    "DataTable/DPStats/Compare/corp/faction/$pack"
    "DataTable/DPStats/Compare/runner/identity/$pack"
    "DataTable/DPStats/Compare/corp/identity/$pack"
    "JSON/Cards/Cardpack/runner/$pack"
    "JSON/Cards/Cardpack/corp/$pack"
    "JSON/Cards/Cardpool/runner/$pack"
    "JSON/Cards/Cardpool/corp/$pack"
    "JSON/DPStats/Identities/runner/$pack"
    "JSON/DPStats/Identities/corp/$pack"
    )
    for packfile in "${packfiles[@]}"
    do
        curl http://localhost:8080/$packfile > "${packfile//%20/ }"
    done
    mkdir "DPStats/${pack//%20/ }"
    curl http://localhost:8080/DPStats/$pack > "DPStats/${pack//%20/ }/index.html"

    mkdir "MDSIdentity/${pack//%20/ }"
    mkdir "DataTable/MDSIdentity/${pack//%20/ }"
    mkdir "JSON/Deck/${pack//%20/ }"
    mkdir "JSON/Average/${pack//%20/ }"

    # for each id
    idsrunner=( $(curl http://localhost:8080/JSON/DPStats/Identities/runner/$pack | jq -r '.[].title' | sed 's/ /%20/g') )
    idscorp=( $(curl http://localhost:8080/JSON/DPStats/Identities/corp/$pack | jq -r '.[].title' | sed 's/ /%20/g') )
    ids=( "${idsrunner[@]}" "${idscorp[@]}" )
    for identity in "${ids[@]}"
    do
        mkdir "MDSIdentity/${pack//%20/ }/${identity//%20/ }"
        curl http://localhost:8080/MDSIdentity/$pack/$identity > "MDSIdentity/${pack//%20/ }/${identity//%20/ }/index.html"
        curl http://localhost:8080/DataTable/MDSIdentity/$pack/$identity > "DataTable/MDSIdentity/${pack//%20/ }/${identity//%20/ }"
        curl http://localhost:8080/JSON/Deck/$pack/$identity > "JSON/Deck/${pack//%20/ }/${identity//%20/ }"
        mkdir "JSON/Average/${pack//%20/ }/${identity//%20/ }"
        curl http://localhost:8080/JSON/Average/$pack/$identity/1 > "JSON/Average/${pack//%20/ }/${identity//%20/ }/1"
        curl http://localhost:8080/JSON/Average/$pack/$identity/2 > "JSON/Average/${pack//%20/ }/${identity//%20/ }/2"
    done
done