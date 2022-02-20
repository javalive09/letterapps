#!/usr/bin/env bash

./gradlew clean
./gradlew assembleRelease

adb uninstall com.javalive09.letterapps
#adb install app/build/outputs/apk/release/*.apk
#adb shell am start -n com.javalive09.letterapps/.MainActivity