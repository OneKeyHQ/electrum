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

if [ -d ${compact_name}/electrum ]; then
	echo "Deleting old ${compact_name}/onekey..."
	rm -fr ${compact_name}/electrum
fi
if [ -d ${compact_name}/electrum_gui ]; then
	echo "Deleting old ${compact_name}/electrum_gui..."
	rm -fr ${compact_name}/electrum_gui
fi
if [ -d ${compact_name}/trezorlib ]; then
	echo "Deleting old ${compact_name}/trezorlib..."
	rm -fr ${compact_name}/trezorlib
fi

echo "Pulling 'onekey' libs into project from ../electrum ..."

cp -fpR ../electrum ${compact_name}/electrum
cp -fpR ../trezor/python-trezor/src/trezorlib ${compact_name}/trezorlib
cp -fpR ../electrum_gui ${compact_name}/electrum_gui
echo "Removing electrum/tests..."
rm -fr ${compact_name}/onekey/electrum/tests
find ${compact_name} -name '*.pyc' -exec  rm -f {} \;

echo ""
echo "Building Briefcase-Based iOS Project..."
echo ""

#mkdir ${HOME}/.briefcase
curl -C - -L "https://briefcase-support.org/python?platform=iOS&version=3.8" -o ${HOME}/.briefcase/Python-3.8-iOS-support.b3.tar

python3.8 setup.py ios --support-pkg=${HOME}/.briefcase/Python-3.8-iOS-support.b3.tar
if [ "$?" != 0 ]; then
	echo "An error occurred running setup.py"
	exit 4
fi

# No longer needed: they fixed the bug.  But leaving it here in case bug comes back!
#cd iOS && ln -s . Support ; cd .. # Fixup for broken Briefcase template.. :/

if [ -d overrides/ ]; then
	echo ""
	echo "Applying overrides..."
	echo ""
	(cd overrides && cp -fpR * ../iOS/ && cd ..)
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
echo "Copying google protobuf paymentrequests.proto to app lib dir..."
echo ""
cp -fa ${compact_name}/electrum/*.proto iOS/app/${compact_name}/electrum/
if [ "$?" != "0" ]; then
	echo "** WARNING: Failed to copy google protobuf .proto file to app lib dir!"
fi
if [ ! -d iOS/Support ]; then
     mkdir iOS/Support
fi

cp -fRa Support/site-package/ iOS/app_packages/
cp -fRa ../electrum/lnwire  iOS/app/${compact_name}/electrum

rm -fr ${compact_name}/electrum/*
rm -fr ${compact_name}/trezorlib/*
rm -fr ${compact_name}/electrum_gui/*
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
