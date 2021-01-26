#!/bin/sh

gpg --quiet --batch --yes --decrypt --passphrase="$ENCRYPT_PASS" --output ./android/release.keystore ./.github/secrets/release.keystore.gpg

