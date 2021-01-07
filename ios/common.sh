#!/bin/bash

# The below are used by the shell scripts as well as setup.py to set things up.
# This is the place to rename the target.app, etc.
#
#
# If you do want to rename the app, edit the 2 variables below, and also:
#
# 1. Make sure to rename the code directory (currently ElectronCash/) in this folder to match compact_name!
# 2. Make sure overrides/ElectronCash gets renamed to exactly match compact_name!

compact_name="OneKey"
xcode_target="OneKey"

function check_env() {
  /usr/bin/env python3.8 --version | grep -q " 3.[6789]"
  if [ "$?" != "0" ]; then
    if /usr/bin/env python3.8 --version; then
      echo "WARNING:: Creating the Briefcase-based Xcode project for iOS requires Python 3.6+."
      echo "We will proceed anyway -- but if you get errors, try switching to Python 3.6+."
    else
      echo "ERROR: Python3.6+ is required"
      exit 1
    fi
  fi

  /usr/bin/env python3.8 -m pip show setuptools >/dev/null
  if [ "$?" != "0" ]; then
    echo "ERROR: Please install setuptools like so: sudo python3.8 -m pip install briefcase"
    exit 2
  fi

  /usr/bin/env python3.8 -m pip show briefcase >/dev/null
  if [ "$?" != "0" ]; then
    echo "ERROR: Please install briefcase like so: sudo python3.8 -m pip install briefcase"
    exit 3
  fi

  /usr/bin/env python3.8 -m pip show cookiecutter >/dev/null
  if [ "$?" != "0" ]; then
    echo "ERROR: Please install cookiecutter like so: sudo python3.8 -m pip install cookiecutter"
    exit 4
  fi

  /usr/bin/env python3.8 -m pip show pbxproj >/dev/null
  if [ "$?" != "0" ]; then
    echo "ERROR: Please install pbxproj like so: sudo python3.8 -m pip install pbxproj"
    exit 5
  fi
  pod >/dev/null
  if [ "$?" != "0" ]; then
    echo "ERROR: Please install pod command-line tools"
    exit 4
  fi
}

function prepare() {
  # workround for Python-iOS-template not support python3.8
  if [ ! -e "${HOME}"/.cookiecutters ]; then
    git clone --single-branch --branch 3.7 https://github.com/beeware/Python-iOS-template "${HOME}"/.cookiecutters/Python-iOS-template
  fi
  if [ ! -e "${HOME}"/.briefcase ]; then
    mkdir "${HOME}"/.briefcase
  fi
  curl -C - -L "https://briefcase-support.org/python?platform=iOS&version=3.8" -o "${HOME}"/.briefcase/Python-3.8-iOS-support.b3.tar
}

function building_template() {
  python3.8 setup.py ios --support-pkg="${HOME}"/.briefcase/Python-3.8-iOS-support.b3.tar
  if [ "$?" != 0 ]; then
    echo "An error occurred running setup.py"
    exit 4
  fi
}

function modify_pinfo_list() {

  infoplist="iOS/${compact_name}/${compact_name}-Info.plist"
  if [ -f "${infoplist}" ]; then
    echo ""
    echo "Adding custom keys to ${infoplist} ..."
    echo ""
    plutil -insert "NSAppTransportSecurity" -xml '<dict><key>NSAllowsArbitraryLoads</key><true/></dict>' -- "${infoplist}"
    if [ "$?" != "0" ]; then
      echo "Encountered error adding custom key NSAppTransportSecurity to plist!"
      exit 1
    fi
    #plutil -insert "UIBackgroundModes" -xml '<array><string>fetch</string></array>' -- "${infoplist}"
    #if [ "$?" != "0" ]; then
    #	echo "Encountered error adding custom key UIBackgroundModes to plist!"
    #	exit 1
    #fi
    longver="4000${GITHUB_RUN_NUMBER:-000}"
    shortver="2.0.3"
    if [ -n "$longver" ]; then
      plutil -replace "CFBundleVersion" -string "$longver" -- "${infoplist}" && plutil -replace "CFBundleShortVersionString" -string "$shortver" -- "${infoplist}"
      if [ "$?" != "0" ]; then
        echo "Encountered error adding custom keys to plist!"
        exit 1
      fi
    fi
    # UILaunchStoryboardName -- this is required to get proper iOS screen sizes due to iOS being quirky AF
    if [ -e "Resources/LaunchScreen.storyboard" ]; then
      plutil -insert "UILaunchStoryboardName" -string "LaunchScreen" -- "${infoplist}"
      if [ "$?" != "0" ]; then
        echo "Encountered an error adding LaunchScreen to Info.plist!"
        exit 1
      fi
    fi
    # Camera Usage key -- required!
    plutil -insert "NSCameraUsageDescription" -string "The camera is needed to scan QR codes" -- "${infoplist}"
    # Bluetooth Usage key -- required!! added by sweepmonkli
    plutil -insert "NSBluetoothAlwaysUsageDescription" -string "The Bluetooth is needed to communication with our hardware." -- "${infoplist}"
    plutil -insert "NSBluetoothPeripheralUsageDescription" -string "The Bluetooth Peripheral is needed to communication with our hardware." -- "${infoplist}"
    # Stuff related to being able to open .txn and .txt files (open transaction from context menu in other apps)
    plutil -insert "CFBundleDocumentTypes" -xml '<array><dict><key>CFBundleTypeIconFiles</key><array/><key>CFBundleTypeName</key><string>Transaction</string><key>LSItemContentTypes</key><array><string>public.plain-text</string></array><key>LSHandlerRank</key><string>Owner</string></dict></array>' -- "${infoplist}"
    plutil -insert "UTExportedTypeDeclarations" -xml '<array><dict><key>UTTypeConformsTo</key><array><string>public.plain-text</string></array><key>UTTypeDescription</key><string>Transaction</string><key>UTTypeIdentifier</key><string>com.c3-soft.OneKey.txn</string><key>UTTypeSize320IconFile</key><string>signed@2x</string><key>UTTypeSize64IconFile</key><string>signed</string><key>UTTypeTagSpecification</key><dict><key>public.filename-extension</key><array><string>txn</string><string>txt</string></array></dict></dict></array>' -- "${infoplist}"
    plutil -insert "UTImportedTypeDeclarations" -xml '<array><dict><key>UTTypeConformsTo</key><array><string>public.plain-text</string></array><key>UTTypeDescription</key><string>Transaction</string><key>UTTypeIdentifier</key><string>com.c3-soft.OneKey.txn</string><key>UTTypeSize320IconFile</key><string>signed@2x</string><key>UTTypeSize64IconFile</key><string>signed</string><key>UTTypeTagSpecification</key><dict><key>public.filename-extension</key><array><string>txn</string><string>txt</string></array></dict></dict></array>' -- "${infoplist}"
    plutil -insert 'CFBundleURLTypes' -xml '<array><dict><key>CFBundleTypeRole</key><string>Viewer</string><key>CFBundleURLName</key><string>onekey</string><key>CFBundleURLSchemes</key><array><string>onekey</string></array></dict></array>' -- "${infoplist}"
    plutil -replace 'UIRequiresFullScreen' -bool NO -- "${infoplist}"
    plutil -insert 'NSFaceIDUsageDescription' -string 'FaceID is used for wallet authentication' -- "${infoplist}"
    plutil -insert 'ITSAppUsesNonExemptEncryption' -bool NO -- "${infoplist}"

    # Un-comment the below to enforce only portrait orientation mode on iPHone
    #plutil -replace "UISupportedInterfaceOrientations" -xml '<array><string>UIInterfaceOrientationPortrait</string></array>' -- "${infoplist}"
    # Because we are using FullScreen = NO, we must support all interface orientations
    plutil -replace 'UISupportedInterfaceOrientations' -xml '<array><string>UIInterfaceOrientationPortrait</string><string>UIInterfaceOrientationLandscapeLeft</string><string>UIInterfaceOrientationLandscapeRight</string><string>UIInterfaceOrientationPortraitUpsideDown</string></array>' -- "${infoplist}"
    plutil -insert 'UIViewControllerBasedStatusBarAppearance' -bool NO -- "${infoplist}"
    plutil -insert 'UIStatusBarStyle' -string 'UIStatusBarStyleLightContent' -- "${infoplist}"
    plutil -insert 'NSPhotoLibraryAddUsageDescription' -string 'Required to save QR images to the photo library' -- "${infoplist}"
    plutil -insert 'NSPhotoLibraryUsageDescription' -string 'Required to save QR images to the photo library' -- "${infoplist}"
    plutil -insert 'LSSupportsOpeningDocumentsInPlace' -bool NO -- "${infoplist}"
  fi
}
function modify_pbxproj() {
  xcode_file="${xcode_target}.xcodeproj/project.pbxproj"
  echo ""
  echo "Mogrifying Xcode .pbxproj file to use iOS 10.0 deployment target..."
  echo ""
  sed -E -i original1 's/(.*)IPHONEOS_DEPLOYMENT_TARGET = [0-9.]+(.*)/\1IPHONEOS_DEPLOYMENT_TARGET = 11.0\2/g' "iOS/${xcode_file}" &&
    sed -n -i original2 '/ASSETCATALOG_COMPILER_LAUNCHIMAGE_NAME/!p' "iOS/${xcode_file}"
  if [ "$?" != 0 ]; then
    echo "Error modifying Xcode project file iOS/$xcode_file... aborting."
    exit 1
  else
    echo ".pbxproj modified ok."
  fi

  echo ""
  echo "Adding HEADER_SEARCH_PATHS to Xcode .pbxproj..."
  echo ""
  python3.8 -m pbxproj flag -t "${xcode_target}" iOS/"${xcode_file}" -- HEADER_SEARCH_PATHS '"$(SDK_DIR)"/usr/include/libxml2'
  if [ "$?" != 0 ]; then
    echo "Error adding libxml2 to HEADER_SEARCH_PATHS... aborting."
    exit 1
  fi

  (cd iOS && python3.8 -m pbxproj folder -t "${xcode_target}" -r -i "${xcode_file}" ../Resources -w -s)
  if [ "$?" != 0 ]; then
    echo "Error adding Resources to iOS/$xcode_file... aborting."
    exit 1
  fi
  (cd iOS && python3.8 -m pbxproj folder -t "${xcode_target}" -r -i "${xcode_file}" ../CustomCode)
  if [ "$?" != 0 ]; then
    echo "Error adding CustomCode to iOS/$xcode_file... aborting."
    exit 1
  fi
}
