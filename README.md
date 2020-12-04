# rotateview

This is an Android library that provides a rotating View.
You can use it when you want to rotate only a part of the View instead of the whole screen.

## Demo
A demo of rotating the terminal and rotating some Views is shown below. The left side is a rotation without animation and the right side is a rotation with animation.

<img src="https://user-images.githubusercontent.com/33488934/101191976-bdfa6080-369d-11eb-967d-12e088a4d4fa.gif" width="200" />

(The animation is not smooth due to rough sampling, but it actually animates smoothly.)


## Setup

### Step 1. Add it in your root build.gradle at the end of repositories.

```
allprojects {
    repositories {
        ...
        maven(url = "https://jitpack.io")
    }
}
```

### Step 2. Add the dependency.

```
dependencies {
    implementation("com.github.KamikazeZirou:rotateview:0.0.1")
}
```

## Usage

### RotateImageView

RotateImageView is an animated and rotating ImageView.

```
<com.kamikaze.rotateview.RotateImageView
    android:id="@+id/rotateImageView"
    android:layout_width="48dp"
    android:layout_height="48dp"
    app:srcCompat="@drawable/ic_baseline_flash_on_24" />
```

### RotateLayout

RotateLayout allows the child views to be rotated (currently, animation is not supported).

```
<com.kamikaze.rotateview.RotateLayout
    android:id="@+id/rotateLayout"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" >

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:scaleType="fitCenter"
            app:srcCompat="@drawable/ic_baseline_flash_on_24" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/flash" />
    </LinearLayout>
</com.kamikaze.rotateview.RotateLayout>
```

### Rotate from Activity

```
class MainActivity : AppCompatActivity() {
    private val listener: OrientationEventListener by lazy {
        object: OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                findViewById<RotateLayout>(R.id.rotateImageView).setOrientation(orientation)
                findViewById<RotateImageView>(R.id.rotateLayout).setOrientation(orientation)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (listener.canDetectOrientation()) {
            listener.enable()
        }
    }

    override fun onStop() {
        super.onStop()
        listener.disable()
    }
}
```

## License

This library includes the work that is distributed in the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Specifically, this library was created based on the source code of the AOSP camera app.
