#!/bin/bash

if [ "$#" -lt "1" ]; then
	echo "usage: mapocado-mapfile-creation <class name>"
	exit 1
fi

DIR=$(dirname $0)
LIBS="$DIR/../cli/build/lib-run"

if [ ! -d "$LIBS" ]; then
	echo "Please run 'gradle createRuntime'"
	exit 1
fi

CLASSPATH="$LIBS/*"

exec java -cp "$CLASSPATH" "$@"
