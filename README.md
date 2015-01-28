# ActivityTransitionManager
一個方便讓你可以使用View過場(切換Activity)的Library


================================================================================
使用教學:

AndroidManifest.xml:

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <application  
      android:theme="@style/AppThemeTranslucent" >


假設今天是Activity A -> Activity B

Activity A:

  切換前把要做過場的元件放入ActivityTransitionManager,裡面是塞View的陣列
  ActivityTransitionManager.getInstance(MainActivity.this).addFormerView(imageView, view);
注意  有些手機切換Activity會有原生動畫,為了不要顯示記得加Flag
  intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
  
Activity B:

  onCreate地方把過場元件的目標位置放入ActivityTransitionManager,裡面是塞View的陣列
  ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView(view,imageView);


注意:Activity A 跟 Activity B之間的過場元件的id必須要一樣才會對應到


================================================================================
ActivityTransitionManager.java 

Function介紹:

  void addFormerView(View... views)  //放入要過場的元件(代表起始位置),呼叫的同時會將view複製出來並且重疊到WindowManager上
  
  void animateFormerViewToLatterView //放入過場後的元件(代表結束的位置),呼叫的同時動畫開始
  
  void setAnimationDuration(int duration)  //設定動畫的時間
  
  boolean isAnimationRunning()  //判斷是否動畫還在進行
  
  setTransparentBackground(boolean transparentBackground) //設定Activity B是否為透明背景
  
  
================================================================================
附註:
  本Library為測試階段,歡迎大家下載試用並且提出改良的方案,希望大家都能創造出漂亮且華麗的UI
  
  
