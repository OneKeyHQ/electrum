#!/bin/bash
set -e
set -u
set -x
. ./common.sh

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

/usr/bin/env python3.8 -m pip show setuptools > /dev/null
if [ "$?" != "0" ]; then
	echo "ERROR: Please install setuptools like so: sudo python3.8 -m pip install briefcase"
	exit 2
fi

/usr/bin/env python3.8 -m pip show briefcase > /dev/null
if [ "$?" != "0" ]; then
	echo "ERROR: Please install briefcase like so: sudo python3.8 -m pip install briefcase"
	exit 3
fi

/usr/bin/env python3.8 -m pip show cookiecutter > /dev/null
if [ "$?" != "0" ]; then
	echo "ERROR: Please install cookiecutter like so: sudo python3.8 -m pip install cookiecutter"
	exit 4
fi

/usr/bin/env python3.8 -m pip show pbxproj > /dev/null
if [ "$?" != "0" ]; then
	echo "ERROR: Please install pbxproj like so: sudo python3.8 -m pip install pbxproj"
	exit 5
fi
pod > /dev/null
if [ "$?" != "0" ]; then
	echo "ERROR: Please install pod command-line tools"
	exit 4
fi
set +x
if [ -d iOS ]; then
	echo "Warning: 'iOS' directory exists. All modifications will be lost if you continue."
	echo "Continue? [y/N]?"
	read reply
	if [ "$reply" != "y" ]; then
		echo "Fair enough. Exiting..."
		exit 0
	fi
	echo "Cleaning up old iOS dir..."
	rm -rf iOS
fi

if [ -d ${compact_name}/electrum ]; then
	echo "Deleting old ${compact_name}/onekey..."
	rm -fr ${compact_name}/electrum
fi
if [ -d ${compact_name}/api ]; then
	echo "Deleting old ${compact_name}/api..."
	rm -fr ${compact_name}/api
fi
if [ -d ${compact_name}/trezorlib ]; then
	echo "Deleting old ${compact_name}/trezorlib..."
	rm -fr ${compact_name}/trezorlib
fi

echo "Pulling 'onekey' libs into project from ../electrum ..."
if [ ! -d ../electrum/locale ]; then
	(cd .. && contrib/make_locale && cd ios)
	if [ "$?" != 0 ]; then
		echo ERROR: Could not build locales
		exit 1
	fi
fi
cp -fpR ../electrum ${compact_name}/electrum
cp -fpR ../trezor/python-trezor/src/trezorlib ${compact_name}/trezorlib
cp -fpR ../electrum_gui ${compact_name}/api
echo "Removing electrum/tests..."
rm -fr ${compact_name}/onekey/electrum/tests
find ${compact_name} -name '*.pyc' -exec  rm -f {} \;

echo ""
echo "Building Briefcase-Based iOS Project..."
echo ""
if [ ! -e  ${HOME}/.briefcase/Python-3.8-iOS-support.b3.tar ]; then
   curl -C -L "https://briefcase-support.org/python?platform=iOS&version=3.8" -o ${HOME}/.briefcase/Python-3.8-iOS-support.b3.tar
fi
python3.8 setup.py ios --support-pkg=${HOME}/.briefcase/Python-3.8-iOS-support.b3.tar
if [ "$?" != 0 ]; then
	echo "An error occurred running setup.py"
	exit 4
fi

# No longer needed: they fixed the bug.  But leaving it here in case bug comes back!
#cd iOS && ln -s . Support ; cd .. # Fixup for broken Briefcase template.. :/

infoplist="iOS/${compact_name}/${compact_name}-Info.plist"
if [ -f "${infoplist}" ]; then
	echo ""
	echo "Adding custom keys to ${infoplist} ..."
	echo ""
	plutil -insert "NSAppTransportSecurity" -xml '<dict><key>NSAllowsArbitraryLoads</key><true/></dict>' -- ${infoplist}
	if [ "$?" != "0" ]; then
		echo "Encountered error adding custom key NSAppTransportSecurity to plist!"
		exit 1
	fi
	#plutil -insert "UIBackgroundModes" -xml '<array><string>fetch</string></array>' -- ${infoplist}
	#if [ "$?" != "0" ]; then
	#	echo "Encountered error adding custom key UIBackgroundModes to plist!"
	#	exit 1
	#fi
	longver=`git describe --tags`
	if [ -n "$longver" ]; then
		shortver=`echo "$longver" | cut -f 1 -d -`
		plutil -replace "CFBundleVersion" -string "$longver" -- ${infoplist} && plutil -replace "CFBundleShortVersionString" -string "$shortver" -- ${infoplist}
		if [ "$?" != "0" ]; then
			echo "Encountered error adding custom keys to plist!"
			exit 1
		fi
	fi
	# UILaunchStoryboardName -- this is required to get proper iOS screen sizes due to iOS being quirky AF
	if [ -e "Resources/LaunchScreen.storyboard" ]; then
		plutil -insert "UILaunchStoryboardName" -string "LaunchScreen" -- ${infoplist}
		if [ "$?" != "0" ]; then
			echo "Encountered an error adding LaunchScreen to Info.plist!"
			exit 1
		fi
	fi
	# Camera Usage key -- required!
	plutil -insert "NSCameraUsageDescription" -string "The camera is needed to scan QR codes" -- ${infoplist}
	# Bluetooth Usage key -- required!! added by sweepmonkli
  plutil -insert "NSBluetoothAlwaysUsageDescription" -string "The Bluetooth is needed to communication with our hardware." -- ${infoplist}
	# Stuff related to being able to open .txn and .txt files (open transaction from context menu in other apps)
	plutil -insert "CFBundleDocumentTypes" -xml '<array><dict><key>CFBundleTypeIconFiles</key><array/><key>CFBundleTypeName</key><string>Transaction</string><key>LSItemContentTypes</key><array><string>public.plain-text</string></array><key>LSHandlerRank</key><string>Owner</string></dict></array>' -- ${infoplist}
	plutil -insert "UTExportedTypeDeclarations" -xml '<array><dict><key>UTTypeConformsTo</key><array><string>public.plain-text</string></array><key>UTTypeDescription</key><string>Transaction</string><key>UTTypeIdentifier</key><string>com.c3-soft.OneKey.txn</string><key>UTTypeSize320IconFile</key><string>signed@2x</string><key>UTTypeSize64IconFile</key><string>signed</string><key>UTTypeTagSpecification</key><dict><key>public.filename-extension</key><array><string>txn</string><string>txt</string></array></dict></dict></array>' -- ${infoplist}
	plutil -insert "UTImportedTypeDeclarations" -xml '<array><dict><key>UTTypeConformsTo</key><array><string>public.plain-text</string></array><key>UTTypeDescription</key><string>Transaction</string><key>UTTypeIdentifier</key><string>com.c3-soft.OneKey.txn</string><key>UTTypeSize320IconFile</key><string>signed@2x</string><key>UTTypeSize64IconFile</key><string>signed</string><key>UTTypeTagSpecification</key><dict><key>public.filename-extension</key><array><string>txn</string><string>txt</string></array></dict></dict></array>' -- ${infoplist}
	plutil -insert 'CFBundleURLTypes' -xml '<array><dict><key>CFBundleTypeRole</key><string>Viewer</string><key>CFBundleURLName</key><string>onekey</string><key>CFBundleURLSchemes</key><array><string>onekey</string></array></dict></array>' -- ${infoplist}
	plutil -replace 'UIRequiresFullScreen' -bool NO -- ${infoplist}
	plutil -insert 'NSFaceIDUsageDescription' -string 'FaceID is used for wallet authentication' -- ${infoplist}
	plutil -insert 'ITSAppUsesNonExemptEncryption' -bool NO -- ${infoplist}

	# Un-comment the below to enforce only portrait orientation mode on iPHone
	#plutil -replace "UISupportedInterfaceOrientations" -xml '<array><string>UIInterfaceOrientationPortrait</string></array>' -- ${infoplist}
	# Because we are using FullScreen = NO, we must support all interface orientations
	plutil -replace 'UISupportedInterfaceOrientations' -xml '<array><string>UIInterfaceOrientationPortrait</string><string>UIInterfaceOrientationLandscapeLeft</string><string>UIInterfaceOrientationLandscapeRight</string><string>UIInterfaceOrientationPortraitUpsideDown</string></array>' -- ${infoplist}
	plutil -insert 'UIViewControllerBasedStatusBarAppearance' -bool NO -- ${infoplist}
	plutil -insert 'UIStatusBarStyle' -string 'UIStatusBarStyleLightContent' -- ${infoplist}
	plutil -insert 'NSPhotoLibraryAddUsageDescription' -string 'Required to save QR images to the photo library' -- ${infoplist}
	plutil -insert 'NSPhotoLibraryUsageDescription' -string 'Required to save QR images to the photo library' -- ${infoplist}
	plutil -insert 'LSSupportsOpeningDocumentsInPlace' -bool NO -- ${infoplist}
fi

if [ -d overrides/ ]; then
	echo ""
	echo "Applying overrides..."
	echo ""
	(cd overrides && cp -fpR * ../iOS/ && cd ..)
fi

stupid_launch_image_grr="iOS/${compact_name}/Images.xcassets/LaunchImage.launchimage"
if [ -d "${stupid_launch_image_grr}" ]; then
	echo ""
	echo "Removing deprecated LaunchImage stuff..."
	echo ""
	rm -fvr "${stupid_launch_image_grr}"
fi

xcode_file="${xcode_target}.xcodeproj/project.pbxproj"
echo ""
echo "Mogrifying Xcode .pbxproj file to use iOS 10.0 deployment target..."
echo ""
sed  -E -i original1 's/(.*)IPHONEOS_DEPLOYMENT_TARGET = [0-9.]+(.*)/\1IPHONEOS_DEPLOYMENT_TARGET = 11.0\2/g' "iOS/${xcode_file}" && \
  sed  -n -i original2 '/ASSETCATALOG_COMPILER_LAUNCHIMAGE_NAME/!p' "iOS/${xcode_file}"
if [ "$?" != 0 ]; then
	echo "Error modifying Xcode project file iOS/$xcode_file... aborting."
	exit 1
else
	echo ".pbxproj mogrifid ok."
fi

echo ""
echo "Adding HEADER_SEARCH_PATHS to Xcode .pbxproj..."
echo ""
python3.8 -m pbxproj flag -t "${xcode_target}" iOS/"${xcode_file}" -- HEADER_SEARCH_PATHS '"$(SDK_DIR)"/usr/include/libxml2'
if [ "$?" != 0 ]; then
	echo "Error adding libxml2 to HEADER_SEARCH_PATHS... aborting."
	exit 1
fi

resources=Resources/*
if [ -n "$resources" ]; then
	echo ""
	echo "Adding Resurces/ and CustomCode/ to project..."
	echo ""
	cp -fRa Resources CustomCode podfile iOS/
	(cd iOS && python3.8 -m pbxproj folder -t "${xcode_target}" -r -i "${xcode_file}" Resources)
	if [ "$?" != 0 ]; then
		echo "Error adding Resources to iOS/$xcode_file... aborting."
		exit 1
	fi
	(cd iOS && python3.8 -m pbxproj folder -t "${xcode_target}" -r -i "${xcode_file}" CustomCode)
	if [ "$?" != 0 ]; then
		echo "Error adding CustomCode to iOS/$xcode_file... aborting."
		exit 1
	fi
fi

so_crap=`find iOS/app_packages -iname \*.so -print`
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
cat Support/main.m > ${main_m}
pch="iOS/${compact_name}/${compact_name}-Prefix.pch"
echo '
//  Prefix header
//
//  The contents of this file are implicitly included at the beginning of every source file.
//

#import <Availability.h>

#ifndef __IPHONE_3_0
#warning "This project uses features only available in iOS SDK 3.0 and later."
#endif

#ifdef __OBJC__

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "OneKeyImport.h"

#endif
' > ${pch}

echo ""
echo "Copying google protobuf paymentrequests.proto to app lib dir..."
echo ""
cp -fa ${compact_name}/electrum/*.proto iOS/app/${compact_name}/electrum/
if [ "$?" != "0" ]; then
	echo "** WARNING: Failed to copy google protobuf .proto file to app lib dir!"
fi
if [ ! -d iOS/Support ]; then
     mkdir iOS/Support
fi

mv iOS/BZip2 iOS/OpenSSL iOS/Python iOS/XZ iOS/VERSIONS iOS/Support/
cp -fa  Support/CFFI  iOS/app/${compact_name}/CFFI
cp -fa  Support/bitarray  iOS/app/${compact_name}/bitarray
cp -fa  Support/LRU  iOS/app/${compact_name}/LRU
cp -fRa Support/site-package/ iOS/app_packages/
cp -fRa ../electrum/lnwire  iOS/app/${compact_name}/electrum

rm -fr ${compact_name}/electrum/*
rm -fr ${compact_name}/trezorlib/*
rm -fr ${compact_name}/api/*
find iOS/app/${compact_name} -name '*.pyc' -exec  rm -f {} \;

cd iOS && pod install
if [ "$?" != "0" ]; then
			echo "Encountered an error when execute pod install!"
			exit 1
		fi
# Can add this back when it works uniformly without issues
# /usr/bin/env ruby update_project.rb

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
