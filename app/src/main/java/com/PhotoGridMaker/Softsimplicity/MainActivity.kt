package com.PhotoGridMaker.Softsimplicity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.PhotoGridMaker.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.head_title_bar.*
import org.jetbrains.anko.toast
import java.io.File

class MainActivity : BaseActivity(), View.OnClickListener {

    private val baseDir = Environment.getExternalStorageDirectory().toString() + "/Deepak/ImageToShare"
    private val tempFile = File(baseDir, "crop_temp")

    companion object {
        val baseDir = Environment.getExternalStorageDirectory().toString() + "/Deepak/ImageToShare"
        val tempFile = File(baseDir, "crop_temp")
        const val REQUEST_CODE_PICK = 0
        const val REQUEST_CODE_CUT = 1
        const val REQUEST_PERMISSION = 2
    }

    override fun onClick(v: View?) {
        when {
            v!!.id == R.id.btn_choose_pic -> {
                //
                //Select image click event
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, REQUEST_CODE_PICK)
            }
        }
    }

    override fun setlayoutIds() {
        setContentView(R.layout.activity_main)
    }

    override fun initViews() {
        head_back_iv.visibility = View.GONE
        setTitle(R.string.main_title)
        val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load(R.drawable.eg).apply(options).into(iv_inside)
        btn_choose_pic.setOnClickListener(this)
        val baseDirFile = File(baseDir)
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Determine if permissions have been granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //This method will return true if the application previously requested this permission but the user rejected the request.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {// Here you can write a dialog box and other items to explain to the user why you want to apply for permission, and then apply for permission again after the confirmation button of the dialog box.
                    toast(R.string.get_permission)
                }
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when {
                requestCode == REQUEST_CODE_PICK -> {
                    //Bring the data to the next page
                    var intent = Intent(this@MainActivity, ChoosePicActivity::class.java)
                    intent.data = data!!.data
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    toast("Permission application failed")
                    finish()
                    break
                }
            }
        }
    }
}
