#!/bin/bash

set -e

rm -rf patterns
rm -rf symbols
cp -LR patterns.original patterns
cp -LR symbols.original symbols 
mapocado styles style-changer \
	-input_classes classes.original.xml -output_classes classes.xml \
	-input_labels labels.original.xml -output_labels labels.xml \
	-mode rotate_hue -angle 300 patterns symbols
./create.package.sh
