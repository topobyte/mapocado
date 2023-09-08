#!/bin/bash

for f in $(ls | grep "style-.*.zip" | grep -v "nolabels"); do
	echo $f;
	cp "$f" ../../../../tools/res/cityviewer
done
