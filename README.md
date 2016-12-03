## 简易五谷分类器

> IDE: android studio 2.2.2
> 
> OS: ubuntu 16.04

一个模式识别课程小设计，使用手机端识别从[这个仓库](https://github.com/lixingcong/Grains-SVM-python)训练得到已识别谷物。

使用SVM分类器，提取谷物的几个特征进行简单分类的搭撒

### 依赖

主要是图像库
- opencv4android 2.4.13.1

### 使用方法

- 下载[opencv4android](https://sourceforge.net/projects/opencvlibrary/files/opencv-android/)，并解压。这里用的是2.4.13.1版本
- 解压得到OpenCV-android-sdk/sdk/native/libs/下面的对应平台的so动态链接库，拷贝到工程app/src/main/jniLibs/下。使得目录结构如下

		app/src/main/jniLibs/
		├── armeabi-v7a
		│   ├── libopencv_info.so
		│   └── libopencv_java.so
		└── x86
			├── libopencv_info.so
			└── libopencv_java.so

- 刷新android studio工程（File->Synchronize）。
- 修改gradle.properties，增加一行

		android.useDeprecatedNdk=true

- Android Virtual Device下载对应CPU架构的虚拟机（可选）以方便调试。比如我是x86，不选x86_64，因为cv库没有这个x64平台

### C和gamma值的调优

一般在电脑端进行训练并交叉验证。参考[这个仓库](https://github.com/lixingcong/Grains-SVM-python)训练样本。

对不同的训练采用不同的交叉验证，以获取最佳的C和gamma。在RBF核下这两个值决定了SVM分类器的性能。

使用[libsvm](http://www.csie.ntu.edu.tw/~cjlin/libsvm/)进行调优。

日期：2016-12-3