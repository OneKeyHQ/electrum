#!/bin/bash

. ./common.sh

if [ ! -d iOS ]; then
    echo "Error: No iOS directory"
    exit 1
fi

cp -fRa iOS/"${compact_name}".xcodeproj  Support/
cp -f iOS/podfile Support/podfile
