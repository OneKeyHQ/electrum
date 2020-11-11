OneKey, iOS Native UI
============================

This subdirectory implements an iOS native UI for OneKey.
It uses the 'Briefcase' project to create an Xcode project which contains within it a Python interpreter, plus all scripts and dependent python packages.  Python 3.6 or above is recommended.

- Rubicon-iOS Web Page: https://pybee.org/project/projects/bridges/rubicon/
- Briefcase Web Page: https://pybee.org/project/projects/tools/briefcase/

Quick Start Instructions
------------------------
1. Requirements:

   * MacOS 12.1  is required with Xcode installed
   * Python 3.8 must be installed
   * cookiecutter, briefcase, pbxproj, and setuptools python packages must be installed::

           python3.8 -m pip install 'setuptools==40.6.2' --user
           python3.8 -m pip install 'cookiecutter==1.6.0' --user
           python3.8 -m pip install 'briefcase==0.2.6' --user
           python3.8 -m pip install 'pbxproj==2.5.1' --user
2. ReSign the binary dependencies
        sh coderesign.sh

3. Generate the iOS project using the included shell script::

           ./make_ios_project.sh

App Store and Ad-Hoc Distribution
---------------------------------
For reasons that aren't entirely clear to me (but likely due to the way libPython.a and other libs are built), you need to do some special magic for the "Release" build to actually run properly. This means that if you want to compile for the App Store or for Ad-Hoc distribution, you need to disable symbol stripping of the compiled binary.  Make sure the following build settings for the "Release" build are as follows:

 - **Strip Debug Symbols During Copy** = NO
 - **Strip Linked Product** = NO
 - **Strip Style** = Debugging Symbols
 - **Enable Bitcode** = NO
 - **Valid Architectures** = arm64
 - **Symbols Hidden by Default** = NO

For more information, see this stackoverflow post: https://stackoverflow.com/questions/22261753/ios-app-wont-start-on-testflight-ad-hoc-distribution

Additional Notes
----------------
The app built by this Xcode project is a fully running standalone OneKey as an iPhone app.!
