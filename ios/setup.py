#!/usr/bin/env python3
import io
import re
from setuptools import setup, find_packages
import sys

with io.open('./common.sh') as f:
    contents = f.read()
    name_match = re.search(r"^ *compact_name *= *['\"]([^'\"]*)['\"]", contents, re.M)
    if name_match:
        compact_name = name_match.group(1)
    else:
        raise RuntimeError("Unable to find compact_name in ./common.sh")

    formal_name_match = re.search(r"^ *xcode_target *= *['\"]([^'\"]*)['\"]", contents, re.M)
    if formal_name_match:
        formal_name = formal_name_match.group(1)
    else:
        raise RuntimeError("Unable to find xcode_target in ./common.sh")
    del name_match, formal_name_match, contents

version_py = './{}/electrum/version.py'.format(compact_name)
with io.open(version_py, encoding='utf8') as version_file:
    version_match = re.search(r"^ *PACKAGE_VERSION *= *['\"]([^'\"]*)['\"]", version_file.read(), re.M)
    if version_match:
        version = version_match.group(1)
    else:
        raise RuntimeError("Unable to find PACKAGE_VERSION in {}.".format(version_py))
    del version_match, version_py

with io.open('README.rst', encoding='utf8') as readme:
    long_description = readme.read()

setup(
    name=compact_name,  # comes from common.sh
    version=version,
    description='A Cold Hardware Wallet',
    long_description=long_description,
    author='Calin Culianu',
    author_email='calin.culianu@gmail.com',
    license='MIT license',
    package_data={
        '': ["*.json", "*.po", "*.mo", "*.pot", "*.txt", "locale/*", "locale/*/*", "locale/*/*/*", "wordlist/*.txt",
             "*.png"]},
    include_package_data=True,
    packages=find_packages(
        exclude=[
            'docs', 'tests',
            'windows', 'macOS', 'linux',
            'iOS', 'android',
            'django'
        ]
    ),
    classifiers=[
        'Development Status :: 1 - Planning',
        'License :: OSI Approved :: MIT license',
        'Programming Language :: Python :: 3.6',
    ],
    install_requires=[
        'aiohttp-socks==0.3.9', 'aiohttp==3.6.2', 'aiorpcX==0.18.4', 'async-timeout==3.0.1', 'attrs==19.3.0',
        'bitstring==3.1.7', 'certifi', 'chardet==3.0.4', 'dnspython==2.0.0', 'ecdsa==0.15', 'helpdev==0.7.1',
        'requests==2.21.0', 'urllib3==1.24.3', 'websockets','async_timeout==3.0.1', 'mnemonic==0.19', 'click==7.0', 'construct==2.9.45',
        'libusb1==1.7.1', 'libusb==1.0.22b2', 'importlib-metadata==1.6.1', 'multidict==4.7.4', 'protobuf==3.12.2', 'pyaes==1.6.1', 'QDarkStyle==2.8.1',
        'qrcode==6.1', 'QtPy==1.9.0', 'six==1.15.0', 'typing-extensions==3.7.4.2', 'yarl==1.4.2', 'zipp==3.1.0', 'colorama==0.4.3', 'cffi',
        'coincurve==13.0.0', 'pycparser==2.19', 'asn1crypto==0.24.0',
    ],
    options={
        'app': {
            'formal_name': formal_name,  # comes from common.sh
            'bundle': 'com.c3-soft'
        },

        # Mobile deployments
        'ios': {
            'app_requires': [
                'rubicon-objc'
            ]
        },
    }
)