#!/bin/bash

ASSEMBLY=customer.dll
SCL=rh-dotnet20
DIR="$(dirname "$(readlink -f "$0")")"

scl enable $SCL -- dotnet "$DIR/$ASSEMBLY" "$@"