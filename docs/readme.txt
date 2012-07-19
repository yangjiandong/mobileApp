android app
===========

2012.07.19
----------

   1. logonActivity 实现OnSharedPreferenceChangeListener

   2. 学习android 取数过程,一般通过thread实现取数,然后通过handler根据结果实现显示
   activity/ConnectServerWithProgressActivity

2012.07.13
----------

   1. 模拟键盘
   InputDemoActivity
   inputkey.xml

   2. 自动更新
   LogonActivity

2012.07.12
----------

   1. 增加记录手机操作信息
   MobLogAction.mobLogInfo,mobLogError

2012.07.10
----------

   1. 读短信
   SMSReceiver

   2. 增加ssh-mobileCommon 公共库支持,取消com.ek.mobileapp.model
   --elcipse下建立link souce,mobilecommon

   --手工发布时需拷贝sshapp-mobileCommon-6.0.jar 到libs

2012.07.09
----------

   1. 还原libs目录,采用maven 方式时调试不能把lib打包到项目

2012.07.08
----------

   1. 增加preference 设置界面
   activity/SettingActivity
   xml/setting.xml

   参考 http://www.javacodegeeks.com/2011/01/android-quick-preferences-tutorial.html

   --sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
   //getSharedPreferences(SettingsUtils.PreferencesString, MODE_PRIVATE);

2012.07.07
----------

   1. 增加actinbar 支持
   svn: sshapp/trunk/android-actionbar-library

2012.07.06
----------

   1. mvn
   mvn install:install-file -Dfile=achartengine-1.0.0.jar -DgroupId=org.achartengine -DartifactId=achartengine -Dversion=1.0.0 -Dpackaging=jar

   其他功能
   混淆 https://code.google.com/p/maven-android-plugin/wiki/ProGuard

   --暂时不能解决生成eclipse项目

2012.07.03
----------

   1. 参考KingSoftMobile 建立项目

   lib/fastjson,achartengine

   2. 静态九宫格布局
   MainActivity

   --END