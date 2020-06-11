package com.example.googlemapsexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_cam_pic.*

import java.io.IOException


class CamPic : AppCompatActivity() {

            private val PERMISSION_CODE = 1000
            private val IMAGE_CAPTURE_CODE = 1001
            var image_uri: Uri? = null
    var latitude: Double? = null
    var longitude: Double? = null


            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_cam_pic)


                capture_btn.setOnClickListener {

                    //if system os is Marshmallow or Above, we need to request runtime permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){

                            //permission was not enabled
                            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            //show popup to request permission
                            requestPermissions(permission, PERMISSION_CODE)
                        }
                        else{
                            //permission already granted
                            openCamera()
                        }
                    }
                    else{
                        //system os is < marshmallow
                        openCamera()
                    }
                }
            }

    private fun openCamera() {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                //camera intent
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
            }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

                //called when user presses ALLOW or DENY from Permission Request Popup
                when(requestCode){
                    PERMISSION_CODE -> {
                        if (grantResults.size > 0 && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED){
                            //permission from popup was granted
                            openCamera()
                        }
                        else{

                            //permission from popup was denied
                            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //called when image was captured from camera intent

        if (resultCode == Activity.RESULT_OK){
            //set image captured to image view
            PhotoCaptured.setImageURI(image_uri)


            //get image description
            val imageRealPath =  getRealPathFromURI(image_uri!!)
            val exif = ExifInterface(imageRealPath)
            val latLong = FloatArray(2)

            try {
                if (exif.getLatLong(latLong)) {
                    LatID.text="Latitude: "+latLong[0].toString()
                    LongID.text="Longitude: "+latLong[1].toString()

                }
            } catch (e: IOException) {
                Log.d("CamPic","Couldn't read exif info: " + e.getLocalizedMessage())
            }

            latitude=latLong[0].toDouble()
            longitude=latLong[1].toDouble()




        }
    }



}


