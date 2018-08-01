BubbleTextureView
==============
Custom bubble shape TextureView for Android,  OpenGL surface implementation using TextureView. 自定义气泡形状的TextureView,使用OpenGL实现

![screenshot1~](https://raw.github.com/zolad/BubbleTextureView/master/screenshot/screenshot_1.gif)

Features
==============
- Custom radius,arrow position and arrow size
- shape textureview by opengl

Dependency
==============
### Add this in your build.gradle file 
```gradle
compile 'com.zolad:bubbletextureview:1.0.0'
```

Usage
==============
### 1.layout xml

```java

  <com.zolad.bubbletextureview.BubbleTextureView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

  
```


### 2.Set radius, arrow size,arrow direction(left or right),arrow position


```java

  /**
     * set corner radius  and  arrow size and arrow direction
     *
     * @param radius       float,pixels,  the corner radius of each corner.
     * @param arrowSize    float,range (0f<arrowSize<1.0f) the size percent of arrow
     * @param arrowOffsetFromCenter  float,range  (-1.0f<arrowSize<1.0f) arrow offset from center
     * @param arrowDirection  boolean,true is left,false is right, the direction of arrow
     */
  mBubbleTextureView.setCornerRadiusAndArrow(40,0.12f, 0.6f,true);
  
  /**
    * use surface in this callback
    */
  mBubbleTextureView.setSurfaceListner(new SurfaceListener() {
            @Override
            public void onSurfaceCreated(SurfaceTexture surface) {
               
               //use surface
            }
        });
        
```


License
==============

    Copyright 2018 Zolad

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
