#!/bin/bash

RULES="../rules/default"
STYLE="../styles/style-train.zip"
OUTPUT="train"

echo "creating html"
mapocado-tools create-html \
	-output "$OUTPUT" \
	-rules "$RULES" \
	-style "$STYLE"
