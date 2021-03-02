#!/bin/bash

RULES="../rules/default"
STYLE="../styles/style-default.zip"
OUTPUT="default"

echo "creating html"
mapocado-tools create-html \
	-output "$OUTPUT" \
	-rules "$RULES" \
	-style "$STYLE"
