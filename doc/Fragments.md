Using Fragments with the Catchoom-SDK
-------------------------------------

Note that our SDK supports android versions from API level 9 (Gingerbread 2.3). Since Fragments require API Level 11 or greater, we're using the support library 'android-support-v4.jar'. To use Fragments, you must include this library in your project libs/ folder. 

Also note that the activity that creates your Fragment must extend from FragmentActivity. 
  
This quick tutorial shows you how to use Fragments with the Catchoom Android SDK. We will focus on using the class `CatchoomSingleShotFragment`. Changing to `CatchoomFinderFragment` is straightforward. Here we're just focusing on how to use Fragments. 

* Create your Activity layout, indicating which class represents your Fragment. In this case, we have a class called `SingleShotFragment` in the package 'com.catchoom.crsexample.fragment'. In this example, we have created a file 'single_shot_camera.xml' that contains:

```xml
        <?xml version="1.0" encoding="utf-8"?>
        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:background="@android:color/background_dark" >
            <fragment android:name="com.catchoom.crsexample.fragment.SingleShotFragment"
            	android:id="@+id/camera_fragment"
            	android:layout_width="match_parent"
            	android:layout_height="match_parent">
        	</fragment>
        </LinearLayout>
```

* Create your FragmentActivity that uses this layout.  

```java
        ...
        import android.support.v4.app.FragmentActivity;
        
        public class FinderFragmentActivity extends FragmentActivity{
          @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.single_shot_camera);
            }
        }
```
* Create your Fragment layout, including the container for the camera preview, the button to take pictures, etc. File 'single_shot_fragment.xml':

        
```xml
        <?xml version="1.0" encoding="utf-8"?>
        <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/background_dark" >
             <FrameLayout
                 android:id="@+id/camera_preview"
                 android:layout_width="match_parent"
                 android:layout_height="match_parent" >
             </FrameLayout>
             <Button
                 android:id="@+id/cameraButton"
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:layout_alignParentBottom="true"
                 android:text="Take picture" />
        </RelativeLayout>
        
```

* Make your 'SingleShotFragment' class extend from `CatchoomSingleShotFragment`. Implement all the functionallities regarding the Catchoom classes here.

```java
        public class SingleShotFragment extends CatchoomSingleShotFragment {
        	private Context mContext;
        	private View fragmentView;
        
        	@Override
        	public View onCreateView(LayoutInflater inflater, ViewGroup container,
        			Bundle savedInstanceState) {
        		super.onCreate(savedInstanceState);
        		fragmentView = inflater.inflate(R.layout.single_shot_fragment, container,
        				false);
        		Activity parentActivity = getActivity();
        		mContext = (Context) parentActivity;
        		
        		//Setup the camera, create your catchoom object, etc.
        		...
        
        		return fragmentView;
        	}
        
        }
        
```

Please, go to the [example app](https://github.com/Catchoom/catchoom-example-android/blob/master/README.md#example-app) to see how to use the Catchoom SDK to capture images and search them over the Catchoom Recognition Service.
