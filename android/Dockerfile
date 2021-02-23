# This image must be built in the context of the repository root.

FROM ubuntu:18.04
SHELL ["/bin/bash", "-c"]
WORKDIR /root

# use aliyun source
ADD sources-aliyun.com.list /etc/apt/sources.list

RUN apt-get update && \
    apt-get install -y gettext openjdk-8-jdk-headless unzip wget
RUN echo "progress=dot:giga" > .wgetrc

# install Python 3.8
RUN apt install -y python3.8 libpython3.8-minimal libpython3.8-dev libpython3.8-stdlib python3-distutils

RUN filename=sdk-tools-linux-4333796.zip && \
    wget https://dl.google.com/android/repository/$filename && \
    mkdir android-sdk && \
    unzip -q -d android-sdk $filename && \
    rm $filename

# Indicate that we accept the Android SDK license. The platform version here isn't critical:
# all versions require the same license, and if app/build.gradle specifies a different version,
# the build process will automatically download it.
RUN yes | android-sdk/tools/bin/sdkmanager "platforms;android-29"

# The app itself. Specifically check for the keystore, otherwise it'll build an APK with no
# certificates.
COPY android android
COPY contrib/deterministic-build contrib/deterministic-build

RUN echo "sdk.dir=$(pwd)/android-sdk" > android/local.properties
