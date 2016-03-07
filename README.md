# Amazon TV#
##Project structure:##

All java files are located  in “\java\tv\theautismchannel\amazon\”. All the xml files(anmations, strings, layouts) and drawable resources are located in “\res\” folder.
###Java files:###

CollectionDemoActivity.java – it is main Activity (Program guide), that contains all series.

EpiActivity.java  - Activity with list of episodes.

EpiView.java  - single episode element.

PreLoadingActivity.java – Loading Activity. We are looking at when content is loading.

RetrieveFeedTask.java – AsyncTask that downloaded xml files in background.

###Resources:###
anim – folder with animation xml files

drawable - folder with image files like icon etc.

layout - folder with drafts for all Activities

values - folder with standard values we can use in the app. For example strings, colors.

####To run and test the project on the TV emulator:####

1) Download [Android Studio](http://developer.android.com/sdk/index.html)

2) Clone project using git and import it: File-New-Import Project-Choose project folder on disc or download and install [jetbrains-bitbucket-connector](https://bitbucket.org/dmitry_cherkas/jetbrains-bitbucket-connector/downloads)
Installation [instruction](http://www.goprogramming.space/connecting-android-studio-project-with-bitbucket/)

3) Run TV emulator by pressing AVD manager button ![AVD manager button](https://wtcindia.files.wordpress.com/2015/07/screen-shot-2015-07-23-at-2-49-36-pm.png). And start TV emulator

4) Press Shift + F10 to run project on existing emulator.