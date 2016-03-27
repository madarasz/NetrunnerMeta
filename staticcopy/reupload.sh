#!/usr/bin/env bash
# upload to github

#upload DB too
# rm ../netrunner.db/messages.log
# zip -r ../../NetrunnerMetaStatic/db.zip ../netrunner.db/

cd ../../NetrunnerMetaStatic/
DATE=`date +%Y-%m-%d`
git add --all
git commit -m "update $DATE"
git push -u origin master
