package com.rmd.media.ml.tf.facedetection

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.rmd.media.ml.tf.facedetection.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var detector: FirebaseVisionFaceDetector? = null

    private lateinit var binding: ActivityMainBinding
    private var image: FirebaseVisionImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // initializing our firebase in main activity
        FirebaseApp.initializeApp(this)


        // setting an onclick listener to the button so as
        // to request image capture using camera
        binding.cameraButton.setOnClickListener {
            // making a new intent for opening camera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                // if the image is not captured, set
                // a toast to display an error image.
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // after the image is captured, ML Kit provides an
        // easy way to detect faces from variety of image
        // types like Bitmap
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE
            && resultCode == RESULT_OK
        ) {
            val extra = data!!.extras
            val bitmap = extra!!["data"] as Bitmap?
            detectFace(bitmap)
        }
    }

    // If you want to configure your face detection model
    // according to your needs, you can do that with a
    // FirebaseVisionFaceDetectorOptions object.
    private fun detectFace(bitmap: Bitmap?) {
        val options: FirebaseVisionFaceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(
                FirebaseVisionFaceDetectorOptions.ACCURATE
            )
            .setLandmarkMode(
                FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS
            )
            .setClassificationMode(
                FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS
            )
            .build()

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap!!)
            detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // It’s time to prepare our Face Detection model.
        detector!!.detectInImage(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->

                // adding an onSuccess Listener, i.e, in case
                // our image is successfully detected, it will
                // append it's attribute to the result
                // textview in result dialog box.
                var resultText: String? = ""
                var i = 1
                for (face in firebaseVisionFaces) {
                    resultText = resultText + """
                    
                    FACE NUMBER. $i: 
                    """.trimIndent() +
                            ("\nSmile: "
                                    + (face.smilingProbability
                                    * 100) + "%") +
                            ("\nleft eye open: "
                                    + (face.leftEyeOpenProbability
                                    * 100) + "%") +
                            ("\nright eye open "
                                    + (face.rightEyeOpenProbability
                                    * 100) + "%")
                    i++
                }

                // if no face is detected, give a toast
                // message.
                if (firebaseVisionFaces.size == 0) {
                    Toast
                        .makeText(
                            this@MainActivity,
                            "NO FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    val bundle = Bundle()
                    bundle.putString(
                        LCOFaceDetection.RESULT_TEXT,
                        resultText
                    )
                    val resultDialog: DialogFragment = ResultDialog()
                    resultDialog.arguments = bundle
                    resultDialog.isCancelable = true
                    resultDialog.show(
                        supportFragmentManager,
                        LCOFaceDetection.RESULT_DIALOG
                    )
                }
            } // adding an on failure listener as well if
            // something goes wrong.
            .addOnFailureListener {
                Toast.makeText(this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 124
    }
}
