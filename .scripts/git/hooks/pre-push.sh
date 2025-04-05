#!/bin/sh

./gradlew spotlessCheck --daemon

RESULT=$?

git stash pop -q

exit $RESULT