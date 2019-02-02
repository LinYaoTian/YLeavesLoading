# LeavesLoading

## 1. 创意原型

**Gif 原图：** 

![](https://github.com/LinYaoTian/TestLeavesLoading/blob/master/model.gif?raw=true)

**效果图：** 

![](https://github.com/LinYaoTian/TestLeavesLoading/blob/master/test1.gif?raw=true)

基本实现了原图的效果

## 2. 如何添加进项目中

- 方式一：

  下载 `LeavesLoading` Library 拷贝进工程中。

- 方式二：
  
  在 Project 的 build.gradle 中：
  ```groovy
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  在 app 的 build.gradle 中：
  ```groovy
   implementation 'com.github.LinYaoTian:LeavesLoading:1.0.0'
  ```

## 3. 如何使用

### 3.1 简单使用

在布局文件中

```xml
 <com.rdc.leavesloading.LeavesLoading
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/myleaf"/>
```

在 Activity 中

```java
LeavesLoading leavesLoading = findViewById(R.id.myleaf);
leavesLoading.setProgress(50);
```

### 3.2 方法

| 方法名                          | 描述             |
| :--------------------------- | -------------- |
| setLeafSrc(int resId)        | 设置叶子图片         |
| setFanSrc(int resId)         | 设置风扇图片         |
| setProgress(int progress)    | 设置进度           |
| setProgressColor(int color)  | 设置进度条颜色        |
| setLeafNum(int num)          | 设置叶片数目         |
| setLeafFloatTime(long time)  | 设置叶子飘动一个周期所花时间 |
| setLeafRotateTime(long time) | 设置叶子旋转一周所花时间   |
| setFanRotateSpeed(int speed) | 设置风扇旋转速度       |
| setFanStroke(int color)      | 设置风扇外圈颜色       |
| setBgColor(int color)        | 设置背景颜色         |

### 3.3 Attributes属性（在布局文件中调用）

| Attributes      | forma     | describe     |
| --------------- | --------- | ------------ |
| leafSrc         | reference | 叶子图片         |
| leafNum         | integer   | 叶子数目         |
| fanSrc          | reference | 风扇图片         |
| bgColor         | color     | 背景颜色         |
| progress        | integer   | 进度值（0-100）   |
| progressColor   | color     | 进度条颜色        |
| leafFloatSpeed  | integer   | 叶子飘动一个周期所花时间 |
| leafRotateSpeed | integer   | 叶子旋转一周所花时间   |
| fanRotateSpeed  | integer   | 设置风扇旋转速度     |
| fanStrokeColor  | color     | 风扇外圈颜色       |

## 4. About Me

作者掘金博客：[Lin_YT](https://juejin.im/user/59759b3e6fb9a06baf2ee47b)
