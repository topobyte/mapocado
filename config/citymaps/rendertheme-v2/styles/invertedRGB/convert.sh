#!/bin/bash

set -e

rm -rf patterns
rm -rf symbols
cp -LR patterns.original patterns
cp -LR symbols.original symbols 
mapocado-tools style-changer \
	-input_classes classes.original.xml -output_classes classes.xml \
	-input_labels labels.original.xml -output_labels labels.xml \
	-mode invert_rgb patterns symbols
./create.package.sh
