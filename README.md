# face-perception
[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

Foobar is a Android library for dealing with word pluralization.

## Getting Started
### Compatibility
 * *Minimum Android SDK* : face-perception 1.0.0 requires a minimum API level of 21.

### Installation
Download the latest aar from [JCenter](https://bintray.com/darwinfrancis/face-perception-still/com.darwin.face.still/1.0.2) or grab via

Gradle:
```gradle
dependencies {
  implementation 'com.darwin.face.still:still:1.0.2'
}
```

or Maven:

```xml
<dependency>
  <groupId>com.squareup.picasso</groupId>
  <artifactId>picasso</artifactId>
  <version>2.71828</version>
</dependency>
```


## Usage
**Kotlin**
```kotlin
val faceDetector = FaceDetector(listener)
faceDetector.detectFace(bitmap)

private val listener: FaceDetectionListener = object : FaceDetectionListener {

        override fun onFaceDetected(result: Result) {}

        override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {}
}
```
*with FaceOptions*
```kotlin
val faceOption =
    FaceOptions.Builder()
               .enableProminentFaceDetection()
               .enableDebug()
               .build()
faceDetector.detectFace(bitmap,faceOption)
```


**Java**
```java
FaceDetector faceDetector = new FaceDetector(listener);
faceDetector.detectFace(bitmap);

private final FaceDetectionListener listener = new FaceDetectionListener() {
        @Override
        public void onFaceDetected(@NotNull Result result) { }

        @Override
        public void onFaceDetectionFailed(@NotNull FaceDetectionError error, @NotNull String message) { }
};
```
*with FaceOptions*
```java
FaceOptions faceOptions = new FaceOptions.Builder()
                .enableProminentFaceDetection()
                .enableDebug()
                .build();
faceDetector.detectFace(bitmap,faceOptions);
```

### Configure the face detector
Face Detector is currently extended with the following configurations. Instructions on how to use them in your own application are linked below.

| FaceOptions | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `prominentFaceDetection` |  crops a single "prominent face only | boolean | false |
| `cropAlgorithm` |  algorithm used for cropping face | CropAlgorithm | CropAlgorithm.THREE_BY_FOUR |
| `minFaceSize` |  sets the smallest desired face size in percentage | int | 10 |
| `debug` |  enables debug lof | boolean | false |

> CropAlgorithm.THREE_BY_FOUR : Crops the image in three by four ratio

> CropAlgorithm.SQUARE        : Crops the image in square(useful when showing face in circular view)

> CropAlgorithm.LEAST         : Crops the face with minimum possible face area(possibly reduce face overlaps when detecting multiple faces )

### Face detection result
Face Detector provides the following values in Result class

| Result | Description | Type |
| :--- | :--- | :--- |
| `faceCount` |  number of faces croped | int |
| `facePortraits` |  list of face data croped | FacePortrait |
| `face` |  bitmap of the cropped face  | Bitmap |
| `smileProbability` |  giving a probability that the face is smiling  | Float |
| `leftEyeOpenProbability` |  giving a probability that the face's left eye is open  | Float |
| `rightEyeOpenProbability` |  giving a probability that the face's right eye is open  | Float |
| `pixelBetweenEyes` |  number of pixels between the eyes  | Double |
| `faceSizePercentage` |  the size of face relative to the input image  | Float |
| `facePose` |  provides rotation of face in X,Y,Z plan  | FacePose |

## Author
Darwin Francis - @darwinfrancis on GitHub, @darwin-francis on linkedin

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

License
-------

    MIT License

    Copyright (c) 2020 Darwin Francis

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
    
    
    
