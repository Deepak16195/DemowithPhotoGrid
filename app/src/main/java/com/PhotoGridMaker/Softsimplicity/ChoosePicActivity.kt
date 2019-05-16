package com.PhotoGridMaker.Softsimplicity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.PhotoGridMaker.base.BaseActivity
import com.PhotoGridMaker.Softsimplicity.MainActivity.Companion.REQUEST_CODE_CUT
import com.PhotoGridMaker.Softsimplicity.MainActivity.Companion.baseDir
import com.PhotoGridMaker.Softsimplicity.MainActivity.Companion.tempFile
import com.PhotoGridMaker.Softsimplicity.slicer.*
import com.PhotoGridMaker.utils.ShareHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_choose_pic.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * @ProjectName project： CutPicToNine
 * @class package：com.wisdom.cutpictonine
 * @class describe：Select the page for the image
 * @author HanXueFeng
 * @time 2019/3/6 9:44
 * @change
 */
class ChoosePicActivity : BaseActivity(), View.OnClickListener {


    private val ninePicImageViews = ArrayList<ImageView>()
    private val towPickImageViews = ArrayList<ImageView>()
    private val threePickImageViews = ArrayList<ImageView>()
    private val fourPickImageViews = ArrayList<ImageView>()
    private val sixPickImageViews = ArrayList<ImageView>()
    private var currentImageViewList = ninePicImageViews
    private val ninePicBitmapSlicer = NinePicBitmapSlicer()
    private val towPicBitmapSlicer = TowPicBitmapSlicer()
    private val threePicBitmapSlicer = ThreePicBitmapSlicer()
    private val fourPicBitmapSlicer = FourPicBitmapSlicer()
    private val sixPicBitmapSlicer = SixPicBitmapSlicer()
    private var bitmapSlicer: BitmapSlicer = ninePicBitmapSlicer
    private var progressView: View? = null
    private var lastDesBitmaps: List<Bitmap>? = null
    private val bitmapSliceListener = object : BitmapSlicer.BitmapSliceListener {
        override fun onSliceSuccess(srcBitmap: Bitmap, desBitmaps: List<Bitmap>) {
            srcBitmap.recycle()
            bitmapSlicer.setSrcBitmap(null)
            for (imageView in ninePicImageViews) {
                imageView.setImageBitmap(null)
                imageView.visibility = View.GONE
            }
            if (lastDesBitmaps != null) {
                for (lastDesBitmap in lastDesBitmaps!!) {
                    lastDesBitmap.recycle()
                }
            }
            lastDesBitmaps = null
            for (i in currentImageViewList.indices) {
                currentImageViewList[i].setImageBitmap(desBitmaps[i])
                currentImageViewList[i].visibility = View.VISIBLE
            }
            lastDesBitmaps = desBitmaps
            progressView!!.visibility = View.GONE

        }

        override fun onSliceFailed() {
            Toast.makeText(this@ChoosePicActivity, "Slice failure", Toast.LENGTH_SHORT).show()
            progressView!!.visibility = View.GONE

        }
    }

    override fun setlayoutIds() {
        setContentView(R.layout.activity_choose_pic)
    }

    override fun initViews() {
        setTitle(R.string.main_title)
        initImageViews()
        initListener()
        progressView = findViewById(R.id.layout_progress)
        ll_nine.performClick()
    }

    /**
     *  @describe Initialize the in-class listener method
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  14:36
     */
    private fun initListener() {
        ll_nine.setOnClickListener(this)
        ll_six.setOnClickListener(this)
        ll_four.setOnClickListener(this)
        ll_three.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btn_share.setOnClickListener(this)
    }

    private fun initImageViews() {
        ninePicImageViews.add(findViewById(R.id.iv_image1))
        ninePicImageViews.add(findViewById(R.id.iv_image2))
        ninePicImageViews.add(findViewById(R.id.iv_image3))
        ninePicImageViews.add(findViewById(R.id.iv_image4))
        ninePicImageViews.add(findViewById(R.id.iv_image5))
        ninePicImageViews.add(findViewById(R.id.iv_image6))
        ninePicImageViews.add(findViewById(R.id.iv_image7))
        ninePicImageViews.add(findViewById(R.id.iv_image8))
        ninePicImageViews.add(findViewById(R.id.iv_image9))

        towPickImageViews.add(ninePicImageViews[0])
        towPickImageViews.add(ninePicImageViews[1])

        threePickImageViews.add(ninePicImageViews[0])
        threePickImageViews.add(ninePicImageViews[1])
        threePickImageViews.add(ninePicImageViews[2])

        fourPickImageViews.add(ninePicImageViews[0])
        fourPickImageViews.add(ninePicImageViews[1])
        fourPickImageViews.add(ninePicImageViews[3])
        fourPickImageViews.add(ninePicImageViews[4])

        sixPickImageViews.add(ninePicImageViews[0])
        sixPickImageViews.add(ninePicImageViews[1])
        sixPickImageViews.add(ninePicImageViews[2])
        sixPickImageViews.add(ninePicImageViews[3])
        sixPickImageViews.add(ninePicImageViews[4])
        sixPickImageViews.add(ninePicImageViews[5])
    }

    /**
     *  @describe Page callback method
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  11:08
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CUT -> {
                    try {
                        val inputStream = FileInputStream(tempFile)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        tempFile.delete()
                        bitmapSlicer.setSrcBitmap(bitmap)
                            .registerListener(bitmapSliceListener)
                            .slice()
                        progressView!!.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(R.string.get_pic_error)
                        progressView!!.visibility = View.GONE
                        return
                    }
                }
            }
        }
    }

    /**
     *  @describe In-class click event
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  14:38
     */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_nine -> {
                setUIState(
                    listOf(ll_nine, ll_six, ll_four, ll_three)
                    , listOf(tv_nine, tv_six, tv_four, tv_three)
                )
                bitmapSlicer = ninePicBitmapSlicer
                currentImageViewList = ninePicImageViews
                cutPic()
            }
            R.id.ll_six -> {
                setUIState(
                    listOf(ll_six, ll_four, ll_three, ll_nine)
                    , listOf(tv_six, tv_four, tv_three, tv_nine)
                )
                bitmapSlicer = sixPicBitmapSlicer
                currentImageViewList = sixPickImageViews
                cutPic()
            }
            R.id.ll_four -> {
                setUIState(
                    listOf(ll_four, ll_three, ll_nine, ll_six)
                    , listOf(tv_four, tv_three, tv_nine, tv_six)
                )
                bitmapSlicer = fourPicBitmapSlicer
                currentImageViewList = fourPickImageViews
                cutPic()
            }
            R.id.ll_three -> {
                setUIState(
                    listOf(ll_three, ll_nine, ll_six, ll_four)
                    , listOf(tv_three, tv_nine, tv_six, tv_four)
                )
                bitmapSlicer = threePicBitmapSlicer
                currentImageViewList = threePickImageViews
                cutPic()
            }
            R.id.btn_save -> {
                //保存图片的按钮
                savePic()
            }
            R.id.btn_share -> {
                ShareHelper.shareSlicesWeChat(this@ChoosePicActivity, progressView, btn_share)
            }
        }
    }


    /**
     *  @describe
    Set the status of the four large buttons below the interface
     * The first passed parameter is selected
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  14:41
     */
    private fun setUIState(linearLayoutList: List<LinearLayout>, textViewList: List<TextView>) {
        for (i in linearLayoutList.indices) {
            if (i == 0) {
                linearLayoutList[i].setBackgroundColor(ContextCompat.getColor(context, R.color.color_2e2e2e))
                textViewList[i].setBackgroundColor(ContextCompat.getColor(context,R.color.color_2e2e2e))
                textViewList[i].setTextColor(ContextCompat.getColor(context,R.color.white))
            } else {
                linearLayoutList[i].setBackgroundColor(ContextCompat.getColor(context,R.color.color_f0f0f0))
                textViewList[i].setBackgroundColor(ContextCompat.getColor(context,R.color.color_f0f0f0))
                textViewList[i].setTextColor(ContextCompat.getColor(context,R.color.color_2e2e2e))
            }
        }
    }

    /**
     *  @describe Cut method
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  15:06
     */
    private fun cutPic() {
        val uri = intent.data
        var h = 0
        var w = 0
        try {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            h = bitmap.height
            w = bitmap.width
            bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to read image", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        // The following crop=true is set to display in the open Intent VIEW can be cropped
        intent.putExtra("crop", "true")
        //This parameter can be set without specifying the aspect ratio of the crop area
        intent.putExtra("aspectX", bitmapSlicer.aspectX)
        intent.putExtra("aspectY", bitmapSlicer.aspectY)
        //This parameter is set to the size of your imageView
        intent.putExtra("outputX", bitmapSlicer.calculateOutputX(w, h))
        intent.putExtra("outputY", bitmapSlicer.calculateOutputY(w, h))
        //Whether to return a bitmap object
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, REQUEST_CODE_CUT)
    }

    /**
     *  @describe save Picture
     *  @return
     *  @author HanXueFeng
     *  @time 2019/3/6  16:05
     */
    private fun savePic() {
        if (lastDesBitmaps == null) {
            toast("\n" + "Please select an image first")
            return
        }
        progressView!!.visibility = View.VISIBLE
        val parent = File(baseDir)
        val prefix = System.currentTimeMillis().toString() + ""
        val slices = ArrayList<File>()
        if (!parent.exists()) {
            parent.mkdirs()
        }
        Observable.fromArray(*lastDesBitmaps!!.toTypedArray())
            .map { bitmap ->
                val index = lastDesBitmaps!!.indexOf(bitmap)
                val file = File(parent, prefix + "_" + (index + 1) + ".jpg")
                val os = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.close()
                file
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ file ->
                val uri = Uri.fromFile(file)
                Log.d("xsm-save-files", uri.toString())
                slices.add(file)
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            }, { throwable ->
                throwable.printStackTrace()
                Toast.makeText(this@ChoosePicActivity, "\n" + "Export failed", Toast.LENGTH_SHORT).show()
                progressView!!.setVisibility(View.GONE)
                ll_choose.visibility = View.VISIBLE
                ll_success.visibility = View.GONE
                btn_share.visibility = View.GONE
                btn_save.visibility = View.VISIBLE
//  resultView.setVisibility(View.GONE)
            }, {
                progressView!!.setVisibility(View.GONE)
                toast(R.string.pic_save_success)
                ll_choose.visibility = View.GONE
                ll_success.visibility = View.VISIBLE
                btn_share.visibility = View.VISIBLE
                btn_save.visibility = View.GONE
//   resultView.setVisibility(View.VISIBLE)
//                resultTv.setText(Html.fromHtml("<font color=\"#868686\">
//The slice has been saved in</font><font color=\"#33a24e\">" + parent.absolutePath + "</font><font color=\"#868686\">，Click to share to a circle of friends</font>"))
//                resultTv.setTag(slices)
                btn_share.setTag(slices)
            })
    }

}