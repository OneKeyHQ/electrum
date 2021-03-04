#!/bin/bash
set -e
set -u
set -x
. ./common.sh

echo ""
echo "Checking_env"
echo ""
check_env
set +x
if [ -d iOS ]; then
  echo "Warning: 'iOS' directory exists. All modifications will be lost if you continue."
  echo "Continue? [y/N]?"
  read -r reply
  if [ "${reply}" != "y" ]; then
    echo "Fair enough. Exiting..."
    exit 0
  fi
  echo "Cleaning up old iOS dir..."
  rm -rf tmp
  mkdir tmp
  cp -fRa iOS/"${compact_name}".xcodeproj tmp/
  cp -fRa iOS/podfile tmp/
  rm -rf iOS
fi

echo ""
echo "preparing environment for building template"
echo ""

prepare

echo ""
echo "Building Briefcase-Based iOS Project..."
echo ""

building_template

stupid_launch_image_grr=iOS/"${compact_name}"/Images.xcassets/LaunchImage.launchimage
if [ -d "${stupid_launch_image_grr}" ]; then
  echo ""
  echo "Removing deprecated LaunchImage stuff..."
  echo ""
  rm -fvr "${stupid_launch_image_grr}"
fi

so_crap=$(find iOS/app_packages -iname \*.so -print)
if [ -n "${so_crap}" ]; then
  echo ""
  echo "Deleting .so files in app_packages since they don't work anyway on iOS..."
  echo ""
  for a in ${so_crap}; do
    rm -vf "${a}"
  done
fi

echo ""
echo "Modifying main.m to include PYTHONIOENCODING=UTF-8..."
echo ""


if [ ! -d iOS/Support ]; then
  mkdir iOS/Support
fi
cp -fRa tmp/"${compact_name}".xcodeproj iOS/
cp -fRa tmp/podfile iOS/
mv iOS/BZip2 iOS/OpenSSL iOS/Python iOS/XZ iOS/VERSIONS iOS/Support/
cp -fRa Support/site-package/ iOS/app_packages/

rm -rf iOS/"${compact_name}"/*
rm -rf iOS/app

(cd iOS && pod install)
if [ "$?" != "0" ]; then
  echo "Encountered an error when execute pod install!"
  exit 1
fi
