package com.example.googlemapsexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.googlemapsexample.Utilities.EXTRA_LATLONG
import com.example.googlemapsexample.Utilities.Loc
import kotlinx.android.synthetic.main.activity_cam_pic.*
import java.io.IOException
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class CamPic : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri = Uri.EMPTY
    var pic_uri: Uri? = null


    var location = Loc("", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_pic)
        mapBtn.setOnClickListener {

            Log.d("THEN URI", "${image_uri}")

            val mapActivity = Intent(this, MapsActivity::class.java)
            mapActivity.putExtra(EXTRA_LATLONG, location)
            startActivity(mapActivity)
        }

        capture_btn.setOnClickListener {

            //if system os is Marshmallow or Above, we need to request runtime permission
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED

            ) {

                //permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }
            mapBtn.visibility = View.VISIBLE

        }
    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        Log.d("BEFORE URI", "${image_uri}")


        val cursor = contentResolver.query(image_uri, null, null, null)
        var result: String? = ""

        if (cursor == null) {
            Log.d("CURSOR", "CURSOR IS NULL")

        } else {

            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            image_uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, result)

            cursor.close()
        }


        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        setResult(Activity.RESULT_OK)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {

                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //called when image was captured from camera intent

        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            PhotoCaptured.setImageURI(image_uri)


            //get image description

            image_uri = MediaStore.setRequireOriginal(image_uri)
            val stream = contentResolver.openInputStream(image_uri)!!
            var latLong = FloatArray(2)

            if (stream != null) {
                val exif = ExifInterface(stream)
                try {
                    if (exif.getLatLong(latLong)) {
                        LatID.text = "Latitude: " + latLong[0].toString()
                        LongID.text = "Longitude: " + latLong[1].toString()
                    }
                } catch (e: IOException) {
                    Log.d("CamPic", "Couldn't read exif info: " + e.getLocalizedMessage())
                }
                stream.close()
            } else {
                // Failed to load the stream, so return the coordinates (0, 0).
                latLong[0] = 0.0F
                latLong[1] = 0.0F

            }
            location.Latitude=latLong[0].toString()
            location.Longitude=latLong[1].toString()

        }

        else{
            Log.d("NOPE", "${resultCode}")
            Log.d("NOPE", "${requestCode}")
            Log.d("NOPE", "${Activity.RESULT_OK}")


        }
    }
}


