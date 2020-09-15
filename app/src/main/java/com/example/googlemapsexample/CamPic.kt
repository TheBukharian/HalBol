package com.example.googlemapsexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.googlemapsexample.Models.EXTRA_LATLONG
import com.example.googlemapsexample.Models.Loc
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import kotlinx.android.synthetic.main.activity_bottom_sheet.*
import kotlinx.android.synthetic.main.activity_cam_pic.*
import java.io.IOException





class CamPic : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri2: Uri = Uri.EMPTY
    var image_uri1: Uri? = null
    var location = Loc("", "")
    lateinit var selectedTag:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_pic)
        val bottomSheetBehavior=BottomSheetBehavior.from(bottomshheet)
        val rotateIMG=AnimationUtils.loadAnimation(this,R.anim.senimage_rotation)

        bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        sendIMG.startAnimation(rotateIMG)




        DescribeLayout.setOnClickListener {
            if(bottomSheetBehavior.state!=BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        mapBtn.setOnClickListener {
            if(PhotoCaptured.tag=="1"){
                Log.d("THEN URI", "${image_uri2}")
                val mapActivity = Intent(this, MapsActivity::class.java)
                mapActivity.putExtra(EXTRA_LATLONG, location)
                startActivity(mapActivity)
            }else{
                                                                                                       //Build  alert dialog

                val alertBuild= AlertDialog.Builder(this@CamPic)
                alertBuild.setTitle("No Image Selected!")
                alertBuild.setIcon(R.mipmap.ic_launcher)
                alertBuild.setMessage("Go back and capture image.")
                alertBuild.setCancelable(true)
                alertBuild.setPositiveButton("OK"){_,_->
                    Toast.makeText(this@CamPic,"Clicked OK",Toast.LENGTH_LONG)
                }
                        val mAlert=alertBuild.create()
                        mAlert.show()
            }
        }
spinnerView.setOnClickListener {
    spinnerView.showOrDismiss()
    spinnerView.setOnSpinnerItemSelectedListener<String> { index, text ->
        selectedTag=text.toString()
        Toast.makeText(this, "${selectedTag}", Toast.LENGTH_SHORT).show()
    }

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
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED

            ) {

                //permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION

                )

                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }

            Thread.sleep(2500)
            mapBtn.visibility = View.VISIBLE

        }
    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri2 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        image_uri1=image_uri2

        //camera intent

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri2)
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
                if (grantResults.isNotEmpty() && grantResults[0] ==
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPath2(){
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(image_uri2, projection, null, null)
        var result: String? = ""

        if (cursor == null) {
            Log.d("CURSOR", "CURSOR IS NULL or : "+cursor)

        } else {

            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.moveToFirst()
            result = cursor.getString(idx)
            Log.d("CURSOR", "RESULT IS : "+result)

            image_uri2 = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, result)
            Log.d("CURSOR", "Image uri IS : "+image_uri2)
            cursor.close()
        }
    }
    private fun getPath1(contentURI: Uri): String? {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (resultCode == Activity.RESULT_OK && requestCode==IMAGE_CAPTURE_CODE) {
                Log.d("NOPE", "${resultCode}")
                getPath2()

                //set image captured to image view
                PhotoCaptured.setImageURI(image_uri2)

                //get image description

                image_uri2 = MediaStore.setRequireOriginal(image_uri2)
                val stream = contentResolver.openInputStream(image_uri2)!!
                var latLong = FloatArray(2)

                if (stream != null) {
                    val exif = ExifInterface(stream)
                    try {
                        if (exif.getLatLong(latLong)) {
                            LatID.text = "Latitude: " + latLong[0].toString()
                            LongID.text = "Longitude: " + latLong[1].toString()
                        }
                    } catch (e: IOException) {
                        Log.d("CamPic", "Couldn't read exif info: " + e.localizedMessage)
                    }
                    stream.close()
                } else {
                    Log.d("NOPE", "Failed to load the stream, so return the coordinates (0, 0).\n")

                    // Failed to load the stream, so return the coordinates (0, 0).
                    latLong[0] = 0.0F
                    latLong[1] = 0.0F

                }

                val bottomSheetBehavior = BottomSheetBehavior.from(bottomshheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


                location.Latitude = latLong[0].toString()
                location.Longitude = latLong[1].toString()
            }

        }
        else {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
                PhotoCaptured.setImageURI(image_uri1)
                PhotoCaptured.tag = "1"

                //get image description
                val imageRealPath = getPath1(image_uri1!!)


                Toast.makeText(this, getPath1(image_uri1!!), Toast.LENGTH_LONG).show()

                val exif = ExifInterface(imageRealPath)
                var latLong = FloatArray(2)
                try {
                    if (exif.getLatLong(latLong)) {
                        LatID.text = "Latitude: " + latLong[0].toString()
                        LongID.text = "Longitude: " + latLong[1].toString()
                    }
                } catch (e: IOException) {
                    Log.d("CamPic", "Couldn't read exif info: " + e.localizedMessage)
                }

                location.Latitude = latLong[0].toString()
                location.Longitude = latLong[1].toString()

            }
        }

    }
    private fun saveIssueToFirebaseDatabase(){

    }
    private fun uploadImageToFirebaseStorage(){

    }
}
class Issue (val description:String, val tag:String, val problemImageUri:String, val lat:Double, val long:Double)



