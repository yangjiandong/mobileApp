android app
===========

2012.08.13
----------

   1. 取消actionBar,采用actionBarSherlock
   logon.xml,main.xml
   themes.xml, <style name="Light1Theme" parent="Theme.Sherlock.Light">
   colors.xml
   LogonActivity,MainActivity,BaseActivity

2012.08.09
----------

   1. 增加记录日志开关,注意登录前的日志信息不会记录

2012.08.07
----------

   1. 增加移动端更改密码功能
   UserUpdatePwdActivity

2012.08.03
----------

   1. 取消手工设置蓝牙设备,通过程序自动扫描

   2. 调整蓝牙处理
   BlueToothConnector
   DrugCheck,NurseBaseActivity,VitalSign,VitalSignBase

2012.08.02
----------

   1. 通过theme设置style
   res/values/attrs.xml,themes.xml

2012.08.01
----------

   1. 增加下载文件处理
   utils/download

2012.07.31
----------

   1. 增加libprojects目录,存放所需library
   actionbar
   viewPageIndicator(滑屏)

   2. 调整tts程序,只在主界面初始化
   MainActivity --> BaseActivity

2012.07.28
----------

   1. 生命体征数据只取一次

2012.07.27
----------

   1. 暂时取消横竖屏配置

2012.07.26
----------

   1. 程序发布
   退出eclipse
   cmd
   ant clean
   ant release
   生成bin/copy/*.apk

   --每次发布只需要更改AndroidManifest.xml 定义的版本信息

2012.07.24
----------

   1. ant build deploy
   具体操作参考svn/mobileDocs/docs/android/ant.txt
   --实现了签名

   ant clean
   ant release

   2. 护理移动模块程序调整,需实现语音蓝牙功能的,统一继承 NurseBaseActivity

   3. 手说安装包
   http://stephen830.iteye.com/blog/1183326
   http://shoushuo.com/index.html

   4. 保证程序符合android标准,所有涉及服务端的操作一定要采用thread or AsyncTask
   如果不采用以上写法,android 4.0 出现NetworkOnMainThreadException
   --线程只是用来下载数据的和其他操作的，操作完成了发送个Message到Handler()函数，然后再里面处理UI
   example
   handler = new Handler();
   Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        progress.setProgress("anything"); // Update the UI
                    }
                });
            }
        };
        new Thread(runnable).start();

2012.07.20
----------

   1. 增加github android 项目的utils

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