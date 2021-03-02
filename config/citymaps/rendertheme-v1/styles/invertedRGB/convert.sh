#!/bin/bash

rm -rf patterns
rm -rf symbols
cp -LR patterns.original patterns
cp -LR symbols.original symbols 
mapocado-tools style-changer -input original.xml -output classes.xml -mode invert_rgb patterns symbols
./create.package.sh
