- [Catchoom example for Android](#catchoom-example-for-android)
	- [Description](#description)
	- [Requirements](#requirements)
	- [Quick Start](#quick-start)
	- [Example App](#example-app)
	- [Adding the SDK to your app](#adding-the-sdk-to-your-app)
	- [Reporting Issues](#reporting-issues)

Catchoom example for Android
============================

Description
-----------
The project contains the source code of a simple example application that demonstrates the functionality of the Catchoom SDK for Android.

Catchoom SDK allows you to integrate into your mobile application the advanced Image Recognition (IR) capabilities of the [Catchoom Recognition Service (CRS)](https://crs.catchoom.com/).

The integration is as simple as importing the compiled SDK into your project and adding a few lines of code. The SDK takes care of capturing the best images, sending them to the CRS, and receiving the recognition results with the related content.

You can implement one of the two different modes of Image Recognition:

* Single shot mode, where users initiate every recognition request (e.g. by pressing a button).

* Finder mode, allowing continuous scan and automatic recognition of all objects appearing in front of the camera.

More specifically, the SDK implements the following features:

* Accessing the CRS image recognition service via [CRS recognition API](http://catchoom.com/documentation/api/recognition/). This includes sending recognition requests to your collections and getting the results and the associated contents/custom data. Moreover, you can even have the SDK communicating with your own backend service and working as a proxy to the CRS.

* Flexible image and video capturing abstraction layer allowing you to specify the video preview. The SDK manages the video capture and presentation of the augmented reality scene. Moreover, you can easily customize the preview and the rendering of the content associated to the recognised objects.

Requirements
------------
To build the project you will need [Eclipse, the Android SDK and Android ADT tools](http://developer.android.com/sdk/index.html).

The SDK works from Android API level 9 (Android 2.3 Gingerbread), but the project is compiled with Android API 18. If you don’t have it, you must download it from the [Android SDK Manager](http://developer.android.com/tools/help/sdk-manager.html).
  
You will also need our sdk. Please [contact us](http://catchoom.com/developer/sdk/) to obtain a copy.

Quick Start
-----------
The **easiest** way to get started is downloading this project, and trying the example application with your images from the CRS. The example app already contains all dependencies added to the project.

Once you have the SDK (file catchoom-sdk-android.jar), you can get the example app running by following these steps:

1. Clone this repository
2. Import it to your Eclipse workspace:  Click on File-> Import -> Existing Projects into Workspace -> Select root directory-> Select the root of the project and click Finish.
3. Paste the archive catchoom-sdk-android.jar into the libs/ folder.
4. Configure your collection token by setting the variable “token” in the “CatchoomApplication.java” class.

This is enough to start [recognising things](http://catchoom.com/documentation/what-kind-of-objects-do-we-recognize/).

Example App
-----------
Just by adding a few lines in your application, you can enable it to show a camera preview and search by taking a picture or capturing video frames from the camera.  To take pictures you must use the **CatchoomSingleShotActivity** class. To continuously capture video frames you must use the **CatchoomFinderActivity** class.

In this example we will use the **CatchoomSingleShotActivity** to take pictures (for continuous capturing refer to the FinderActivity.java class in the example app). Finally we will explain how to compare the images obtained with the ones in your collection using the **Catchoom** class.


**Capturing query images**

Start by making your activity extend from CatchoomSingleShotActivity instead of android Activity, and implementing the CatchoomImageHandler interface:

```java
    public class SingleShotActivity extends CatchoomSingleShotActivity implements CatchoomImageHandler,CatchoomResponseHandler
``` 
Setup the camera preview by making a call to setCameraParams with your Activity Context and the FrameLayout where you want to place your camera preview.
```java
    Context mContext=(Context) this;
    FrameLayout mPreview = (FrameLayout) findViewById(R.id.camera_preview); 
    setCameraParams(mContext,mPreview);
    setImageHandler((CatchoomImageHandler)this);
```
Call takePicture to take a picture and receive it in your CatchoomImageHandler. 
```java
    mSnapPhotoButton= (Button) findViewById(R.id.cameraButton);
    mSnapPhotoButton.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
            takePicture();
        }
    });
```
When the picture is available, you will receive a call to your requestImageReceived implementation with the picture taken. You can compare the given picture against your collection by calling the function search using a Catchoom object, as we will explain in the next section
```java
    public void requestImageReceived(CatchoomImage image) {
        //Here you can search the given image 
    }
```
Finally, if you want to take more pictures, you have to call restartPreview()  after receiving the requestImageReceived callback.

**Comparing query images against your collection**

Make your activity implement the CatchoomResponseHandler interface. 
```java
    public class SingleShotActivity extends CatchoomSingleShotActivity implements CatchoomImageHandler,CatchoomResponseHandler
```
Create and initialize a Catchoom object and call setResponseHandler to allow your activity to receive responses from the CRS.
```java
    Catchoom catchoom= new Catchoom();
    catchoom.setResponseHandler((CatchoomResponseHandler)this);
```
You can perform search by passing a CatchoomImage and [your collection token](http://catchoom.com/documentation/where-do-i-get-my-token) to your Catchoom object. In this case, we will search for the image we received in the requestImageReceived callback. The request is executed asynchronously, so the operations may take several seconds (between 1 and 5 on average) depending on various factors like the Internet connection, the performance of the device, etc. 
```java
    public void requestImageReceived(CatchoomImage image) {
       catchoom.search("your_collection_token", image);
    } 
```
If the search was successful you will receive a call to your requestCompletedResponse with the results obtained from the CRS. The responseData Object will contain a list of CatchoomSearchResponseItems with all the matches ordered by score. If you want just one result, keep the first match, as it’s the one with highest score. Note that the results list can be empty if no results were found.
```java
    public void requestCompletedResponse(int requestCode, Object responseData) {
        ArrayList<CatchoomSearchResponseItem> results = (ArrayList<CatchoomSearchResponseItem>) responseData;
        if(results.size()>0)
            CatchoomSearchResponseItem bestMatch= results.get(0);
    }
```
A CatchoomSearchResponseItem encapsulates a result in an easy to access class. You can retrieve several fields from the item, such as the name of the item, the score, the thumbnail, the url, etc.
```java
        int score = bestMatch.getScore();
        String id = bestMatch.getId();
        Bundle metadata= bestMatch.getMetadata();
        String name = metadata.getString("name");
        String thumbnailUrl = metadata.getString("thumbnail");
        String url = metadata.getString("url");
        String custom = metadata.getString("custom");
```
A search request can fail for several reasons. If a request fails, you will receive a callback to requestFailedResponse , with a CatchoomErrorResponseItem object describing the failure reason, or null if the connection could not be established.

Now that you are familiar with the SDK, take a look at the provided example app. It contains a scanning-effect you can use while searching, and it shows you how to parse the results.

To switch from the Single Shot to the Finder Mode, you can go to the provided example app and change CAMERA_USED from CAMERA_TYPE_SINGLE_SHOT to CAMERA_TYPE_FINDER in the  CatchoomApplication.java class.

Adding the SDK to your app
--------------------------
The Catchoom Android SDK is distributed as a .jar that you can directly drag into your project. It has, though some dependencies on external libraries that have to be linked:

* sanselan-0.97-incubator.jar

To add the Catchoom Android SDK into your project you must do the following:

1. Open your Android app's project into Eclipse.
2. Create a libs/ folder under your project root directory if it doesn't exist.
3. Paste the archive catchoom-sdk-android.jar into your project libs/ folder.
4. Paste the archive sanselan-0.97-incubator.jar into your project libs/ folder.

Additionally, you have to declare the following permissions in your application’s manifest:
```xml
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.hardware.camera.autofocus" />
```
Reporting Issues
----------------
If you have suggestions, bugs or other issues specific to this library, file them [here](https://github.com/Catchoom/catchoom-sdk-ios/issues) or contact us at [support@catchoom.com](mailto:support@catchoom.com).
