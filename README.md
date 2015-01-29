# ActivityTransitionManager
一個方便讓你可以使用View過場(切換Activity)的Library

![Screenshot](https://raw.githubusercontent.com/guodong1111/ActivityTransitionManager/master/image/animation.gif)

================================================================================
## 使用教學:

### AndroidManifest.xml:

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <application  
        android:theme="@style/NoAnimationTheme" >
      
        <!--有需要透明背景的Activity才使用-->
        <activity
            android:name=".MainActivity2"
            android:theme="@style/AppThemeTranslucent">


假設今天是Activity A -> Activity B

### Activity A:

  切換前把要做過場的元件放入ActivityTransitionManager,裡面是塞View的陣列

```
  ActivityTransitionManager.getInstance(this).addFormerView(view...);

```
  
  
### Activity B:

  onCreate地方把過場元件的目標位置放入ActivityTransitionManager,裡面是塞View的陣列


```
ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView(view...);

```


注意:Activity A 跟 Activity B之間的過場元件的id必須要一樣才會對應到


================================================================================
### ActivityTransitionManager.java 

Function介紹:

```
void addFormerView(View... views)  //放入要過場的元件(代表起始位置),
                                   //呼叫的同時會將view複製出來並且重疊到WindowManager上
                                   //views放越前面的元件會在Windowmanager顯示裡面的越底層
  
void animateFormerViewToLatterView //放入過場後的元件(代表結束的位置),呼叫的同時動畫開始
  
void setAnimationDuration(int duration)  //設定動畫的時間
                                         //呼叫過本funcion過後就會無視原本view設定過的duration 
                                         //ex: view.animate().setDuration();
  
boolean isAnimationRunning()  //判斷是否動畫還在進行
  
void setOnTransitioAnimationListener(OnTransitioAnimationListener onTransitioAnimationListener)    //設定listener回收callback

void setTransitionScreenOffset(int transitionScreenOffset) //客製化是否需要讓過場動畫的起始 高度偏移
```
  
  
================================================================================
## 附註：

  本Library為測試階段,歡迎大家下載試用並且提出改良的方案,希望大家都能創造出漂亮且華麗的UI
  
  

