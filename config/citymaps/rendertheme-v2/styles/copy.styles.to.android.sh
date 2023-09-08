#!/bin/bash

for f in $(ls | grep "style-.*.zip" | grep -v "nolabels"); do
	echo $f;
	cp "$f" ~/github/stadtplan-app/app/src/main/assets/
done
