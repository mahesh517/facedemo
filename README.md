#Face Search Android SDK

Application can run either on device or emulator only in front camera

## Build the demo using Android Studio

### Prerequisites

* You need an Android device and Android development environment with minimum API 21.
* Android Studio 3.2 or later.

### Building or Download
* Open Android Studio, and from the Welcome screen, select Open an existing Android Studio project.

* From the Open File or Project window that appears, import New Module with face_search.aar file.
* If it asks you to do a Gradle Sync, click OK.
* Add file.json file in assets folder
*  You may also need to install various dependencies and tools, if you get errors like "Failed to find target with hash string 'android-21'" and similar.
  Click the Run button (the green arrow) or select Run > Run 'android' from the top menu. You may need to rebuild the project using Build > Rebuild Project.

##  implementation 'org.tensorflow:tensorflow-lite:0.0.0-nightly'
##  implementation 'com.squareup.retrofit2:converter-gson:2.1.0'
##  implementation 'com.squareup.retrofit2:retrofit:2.1.0'
##  implementation 'com.quickbirdstudios:opencv:3.4.1'

* And add this below lines in prject gradle
   aaptOptions {
        noCompress "tflite"
    } 

   
 
## Additional Note
 _Please do not delete or rename  file.json file
 
 ## How do I use FaceSearch?
 
   
 FaceDetection facedetection=new FaceDetection(Context context);

To use it in the code you need only call below  methods

# For Automatic Face search


Simple use cases will look something like this:

## For a initializing 

 FaceDetection facedetection=new FaceDetection(Context context);
 
 facedetection.automaticDetection(Context context);
 
 #For Manual Face Search
 
 FaceDetection facedetection=new FaceDetection(Context context);
 
 facedetection.getFaceresult(Context context, Bitmap faceCroped, ManualSearchResponse manualSearchResponse);
 
 ## For Manual Add User 
 
  FaceDetection facedetection=new FaceDetection(Context context);
  facedetection.addUser(Context context, File file,ManualUserAddInterface manualUserAddInterface);
  
  
  ## For Manual Add User with name
   FaceDetection facedetection=new FaceDetection(Context context);
   facedetection.addUserWithApproval(final Context context, String imagePath, String username, final ManualUserWithApproval userWithApproval)
    
    
 
 