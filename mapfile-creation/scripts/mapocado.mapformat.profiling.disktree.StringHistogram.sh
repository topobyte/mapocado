#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/mapocado-mapfile-creation"
CLASS="de.topobyte.mapocado.mapformat.profiling.disktree.StringHistogram"

exec "$CMD" "$CLASS" "$@"
