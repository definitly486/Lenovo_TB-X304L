#!/bin/sh
APK_PATH="$HOME/AndroidStudioProjects/App/app/build/outputs/apk/debug"
cd $HOME/Lenovo_TB-X304L
mv $APK_PATH/app-debug.apk  $APK_PATH/app304-android10.apk
echo "Y" | gh release  delete-asset apk  app304-android10.apk 
gh release  upload apk $APK_PATH/app304-android10.apk