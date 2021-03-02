#!/bin/bash

set -e

(cd default ; ./create.package.sh)

for f in \
	invertedRGB \
	invertedLuminance \
	shifted-pink \
	blackAndWhiteDark \
	blackAndWhiteLight \
	; do \
	echo $f;
	(cd $f ; ./convert.sh) \
	done
