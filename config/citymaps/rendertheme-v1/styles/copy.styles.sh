#!/bin/bash

for f in $(ls | grep "style-.*.zip" | grep -v "train\|mapnik"); do
	echo $f;
	cp "$f" ~/git/stadtplan-ng/eclipse/assets/
done
