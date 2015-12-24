#!/bin/bash
#set -x
# brew install jq

rm -rf content
mkdir content
cd content

curl http://localhost:8080/ > index.html

sides=( "corp" "runner" )

# create directories without sides
dirs=(
"static"
"static/css"
"static/js"
"static/js/vendor"
"static/img"
"static/img/blog"
"JSON"
"DataTable"
"DataTable/Cardpool"
"DPStats"
"DataTable/DPStats"
"DataTable/DPStats/Top"
"DataTable/DPStats/All"
"DataTable/DPStats/Compare"
"DataTable/MDSIdentity"
"JSON/Cards"
"JSON/Cards/Cardpack"
"JSON/Cards/Cardpool"
"JSON/DPStats"
"JSON/DPStats/Identities"
"JSON/Deck"
"JSON/Average"
"MDSIdentity"
"Blog"
)
for dir in "${dirs[@]}"
do
    mkdir $dir
done

# create directories with sides
for side in "${sides[@]}"
do
    dirs=(
    "DataTable/DPStats/Top/$side"
    "DataTable/DPStats/Top/$side/faction"
    "DataTable/DPStats/Top/$side/identity"
    "DataTable/DPStats/All/$side"
    "DataTable/DPStats/All/$side/faction"
    "DataTable/DPStats/All/$side/identity"
    "DataTable/DPStats/Compare/$side"
    "DataTable/DPStats/Compare/$side/faction"
    "DataTable/DPStats/Compare/$side/identity"
    "JSON/Cards/Cardpack/$side"
    "JSON/Cards/Cardpool/$side"
    "JSON/DPStats/Identities/$side"
    )
    for dir in "${dirs[@]}"
    do
        mkdir $dir
    done
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
"DataTable/Cardpool/corp"
"404"
"soon"
"Info"
"static/img/404doge.png"
"static/img/soon.jpg"
"static/img/knowthemeta20x20.png" )

for file in "${files[@]}"
do
    touch $file
    curl http://localhost:8080/$file > $file
done

mv soon soon.html
mv 404 404.html
mv Info Info.html

# blog
curl http://localhost:8080/Blog > Blog/index.html
blogs=( $(curl http://localhost:8080/JSON/Blog/Teasers | jq -r '.[].url' ) )
for blog in "${blogs[@]}"
do
    mkdir Blog/$blog
    curl http://localhost:8080/Blog/$blog > "Blog/$blog/index.html"
done
cp ../../src/main/resources/static/img/blog/* static/img/blog/


packs=( $(curl http://localhost:8080/JSON/Cardpool | jq -r '.[].title' | sed 's/ /%20/g') )

# for each pack
for pack in "${packs[@]}"
do
    for side in "${sides[@]}"
    do
        packfiles=(
        "DataTable/DPStats/Top/$side/faction/$pack"
        "DataTable/DPStats/Top/$side/identity/$pack"
        "DataTable/DPStats/All/$side/faction/$pack"
        "DataTable/DPStats/All/$side/identity/$pack"
        "DataTable/DPStats/Compare/$side/faction/$pack"
        "DataTable/DPStats/Compare/$side/identity/$pack"
        "JSON/Cards/Cardpack/$side/$pack"
        "JSON/Cards/Cardpool/$side/$pack"
        "JSON/DPStats/Identities/$side/$pack"
        )
        for packfile in "${packfiles[@]}"
        do
            curl http://localhost:8080/$packfile > "${packfile//%20/ }"
        done
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