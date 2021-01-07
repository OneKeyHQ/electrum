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
echo ""
echo "Modify  iOS/${compact_name}/${compact_name}-Info.plist"
echo ""
modify_pinfo_list

if [ -d overrides/ ]; then
  echo ""
  echo "Applying overrides..."
  echo ""
  (cd overrides && cp -fpR * ../iOS/)
fi

stupid_launch_image_grr="iOS/${compact_name}/Images.xcassets/LaunchImage.launchimage"
if [ -d "${stupid_launch_image_grr}" ]; then
  echo ""
  echo "Removing deprecated LaunchImage stuff..."
  echo ""
  rm -fvr "${stupid_launch_image_grr}"
fi

modify_pbxproj

so_crap=$(find iOS/app_packages -iname \*.so -print)
if [ -n "$so_crap" ]; then
  echo ""
  echo "Deleting .so files in app_packages since they don't work anyway on iOS..."
  echo ""
  for a in $so_crap; do
    rm -vf $a
  done
fi

echo ""
echo "Modifying main.m to include PYTHONIOENCODING=UTF-8..."
echo ""

main_m="iOS/${compact_name}/main.m"
if [ -e "${main_m}" ]; then
  rm -f "${main_m}"
fi

pch="iOS/${compact_name}/${compact_name}-Prefix.pch"
if [ -e "${pch}" ]; then
  rm -f "${pch}"
fi

if [ ! -d iOS/Support ]; then
  mkdir iOS/Support
fi

mv iOS/BZip2 iOS/OpenSSL iOS/Python iOS/XZ iOS/VERSIONS iOS/Support/
cp -fRa Support/site-package/ iOS/app_packages/
ln Support/podfile iOS/podfile
ln Support/main.m "${main_m}"
ln Support/"${compact_name}"-Prefix.pch "${pch}"

(cd iOS && pod install)
if [ "$?" != "0" ]; then
  echo "Encountered an error when execute pod install!"
  exit 1
fi

# remove our framework from FrameworksBuildPhase
ruby ./update_project.rb

echo ''
echo '**************************************************************************'
echo '*                                                                        *'
echo '*   Operation Complete. An Xcode project has been generated in "iOS/"    *'
echo '*                                                                        *'
echo '**************************************************************************'
echo ''
echo '  IMPORTANT!'
echo '        Now you need to either manually add the library libxml2.tbd to the '
echo '        project under "General -> Linked Frameworks and Libraries" *or* '
echo '        run the ./update_project.rb script which will do it for you.'
echo '        Either of the above are needed to prevent build errors! '
echo ''
echo '  Also note:'
echo '        Modifications to files in iOS/ will be clobbered the next    '
echo '        time this script is run.  If you intend on modifying the     '
echo '        program in Xcode, be sure to copy out modifications from iOS/ '
echo '        manually or by running ./copy_back_changes.sh.'
echo ''
echo '  Caveats for App Store & Ad-Hoc distribution:'
echo '        "Release" builds submitted to the app store fail unless the '
echo '        following things are done in "Build Settings" in Xcode: '
echo '            - "Strip Debug Symbols During Copy" = NO '
echo '            - "Strip Linked Product" = NO '
echo '            - "Strip Style" = Debugging Symbols '
echo '            - "Enable Bitcode" = NO '
echo '            - "Valid Architectures" = arm64 '
echo '            - "Symbols Hidden by Default" = NO '
echo ''
