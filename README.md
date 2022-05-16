### A Tetris game written in Kotlin, intended to be used with Android.

The core engine of the game is designed to be decoupled from the user interface and provide greater flexibility for UI design.

Development is largely complete, and I am now focusing mostly on improving performance and fixing bugs.

The application is not yet published on Play Store, but I intend to do so after I feel it is stable and more complete.

### Command-line installation instructions for Arch Linux users

Install the package [android-tools](https://archlinux.org/packages/community/x86_64/android-tools/).

[Enable developer mode](https://developer.android.com/studio/debug/dev-options) on your Android device.

Obtain the APK file of the release you want from the [releases page](https://github.com/SpiritualForest/AndroidTetris/releases).

Plug your Android device via USB and issue the command *adb -d install path/to/apk_file*.

For example: *adb -d install ~/downloads/androidtetris-0.2b.apk*

Installation through other Linux distributions should be largely similar, but the android-tools package name may differ, or include different tools.  

Find out how to install the adb tool and android-udev rules on your distribution and follow the rest of the instructions afterwards.
### Gameplay:

![](https://i.imgur.com/nUMGuJz.gif)
