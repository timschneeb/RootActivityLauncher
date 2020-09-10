# RootActivityLauncher
Launch all hidden, restricted and unexported activities in Android via Root


Based on [DerekZiemba's fork](https://github.com/DerekZiemba/RootActivityLauncher) of Adam Szalkowski's Activity Launcher.
The original RootActivityLauncher crashed on all devices running Android 8 or later and was abandoned in 2015.

This project has been fully rewritten in Kotlin and modernized to support later Android versions. 

## Changes
* Using Root, you can launch pretty much any activity (that doesn't require extra intent data)

* Use material theme

* Dark mode support

* Functionality of icon processing and shortcut creation on Android 8 and later restored

* Previously, the RootActivityLauncher had a serious bug which would block loads of hidden activities that were marked as "unexported" and/or "disabled". This version grants access to these restricted activities and adds a "Root only" tag to distinguish them from normal ones


## Credits

* [DerekZiemba's fork](https://github.com/DerekZiemba/RootActivityLauncher)
* [Adam Szalkowski's version of Activity Launcher](https://play.google.com/store/apps/details?id=de.szalkowski.activitylauncher)
  * [Also on Sourceforge](http://sourceforge.net/projects/activitylauncher/)
