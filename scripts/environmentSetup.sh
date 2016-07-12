#!/bin/bash

# Fix the CircleCI path
function getAndroidSDK {
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

  DEPS="$ANDROID_HOME/installed-dependencies"

  if [ ! -e $DEPS ]; then
    echo y | android update sdk --no-ui --all --filter tools &&
    echo y | android update sdk --no-ui --all --filter android-24 &&
    echo y | android update sdk --no-ui --all --filter platform-tools &&
    echo y | android update sdk --no-ui --all --filter build-tools-24.0.0 &&
    echo y | android update sdk --no-ui --all --filter extra-android-m2repository &&
    echo y | android update sdk --no-ui --all --filter "extra-android-support" &&
    echo y | android update sdk --no-ui --all --filter extra-google-m2repository &&
    echo y | android update sdk --no-ui --all --filter sys-img-x86-android-24 &&
    echo y | android update sdk --no-ui --all --filter extra-google-google_play_services &&
    echo no | android create avd -n testAVD -f -t android-19 --abi default/armeabi-v7a &&
    touch $DEPS
  fi
}

# For Android Instrumentation test
function waitForAVD {
  local bootanim=""
  export PATH=$(dirname $(dirname $(which android)))/platform-tools:$PATH
  until [[ "$bootanim" =~ "stopped" ]]; do
    sleep 5
    bootanim=$(adb -e shell getprop init.svc.bootanim 2>&1)
    echo "emulator status=$bootanim"
  done
}