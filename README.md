# Tracker
Tracker 是Android 上的一个用户行为跟踪框架，根据预先订阅的事件链，以观察者模式监听用户的行为，当用户的行为与订阅的一样时，通知给订阅者。

没有看明白？

没关系，直接说明traker的应用场景。“埋点”，当我们的app到一定阶段时，一定会有埋点需求，例如在某按钮点击后统计埋点，进行上报，给到产品，可以统计功能的使用度。还有页面跳转 等等……当然还有复杂的埋点，如页面的多级跳传，A->B->C 。或者多个入口进同一页面的分别埋点A->C， B->C。在埋点服务上，我们一般会选择第三方的服务，如友盟等……，但是埋点代码还是需要我们来写到指定的地方的。这种埋点称为代码埋点。开发人员需要把埋点代码写到业务代码中。在埋点多了后，在重构时，会容易不小心删除埋点，而且也不好管理。当然还有全埋点，也就是在所有的点击事件或页面跳转的地方插入埋点代码，然后一起上报，由专门分析人员从中分析数据。但是这样会造成上传数据量过大，有时候无法精准的统计到某个功能。基于此背景，我们希望能做到按需埋点，但是又能最大程度的不侵入业务代码。使埋点代码集中放到一起管理，而且还要能实现复杂的埋点。所以Tracker（追踪者） 就是这样产生的。

#### 如何接入？

在project中的build.gradle 中
```    
buildscript {
    dependencies {
        ...
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
    }
    allprojects {
	repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}
    }
 }
 ```   
 在app的build.gradle 中
  ```  
 apply plugin: 'com.hujiang.android-aspectjx'
  
 dependencies {
     implementation 'com.github.ttpai:Tracker:1.0.1'
 }
 ```
 在application 中注册
 ```
@Override
public void onCreate() {
    super.onCreate();
    Track.initTrack(getApplication());
}
```
#### 如何使用？
 
 监听AActivity 跳转到 某页面事件：
 ```
Track.from(AActivity.class).to(BActivity.class).subscribe(new OnSubscribe<Intent>() {
    @Override
    public void call(Intent intent) {
        Log.d(TAG, "A->B");
    }
});
  ```
 监听AActivity 中的某view被点击：
 ```
  Track.from(AActivity.class).viewClick(R.id.button).subscribe(new OnSubscribe<View>() {
      @Override
      public void call(View view) {
          Log.d(TAG, "A.click(R.id.button)");
      }
 });
```
#### 更多
由于AOP 是基于 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx) ，在此感谢 aspectjx 项目的贡献
[Tracker Api 操作符](https://github.com/ttpai/Tracker/wiki/Tracker-Api-%E6%93%8D%E4%BD%9C%E7%AC%A6)
[Android Tracker 原理](https://github.com/ttpai/Tracker/wiki/Android-Tracker-%E5%8E%9F%E7%90%86)

