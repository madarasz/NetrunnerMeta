#!/usr/bin/env bash
# backup DB


DATE=`date +%Y-%m-%d`
zip -r ../../KnowTheMetaDB/$DATE.zip ../netrunner.db/
