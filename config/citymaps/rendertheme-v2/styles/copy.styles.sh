#!/bin/bash

for f in $(ls | grep "style-.*.zip" | grep -v "nolabels"); do
	echo $f;
	cp "$f" ~/git/stadtplan-ng3/app/src/main/assets/
done
