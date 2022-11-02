package com.road801.android.common;

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toolbar
import androidx.databinding.BindingAdapter;
import androidx.navigation.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.Writer
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.road801.android.view.home.HomeActivity
import com.road801.android.view.intro.IntroActivity
import java.util.*

@BindingAdapter("navigateUp")
fun bindNavigateUp(toolbar: Toolbar, listener: View.OnClickListener) {
    toolbar.setNavigationOnClickListener { toolbar.findNavController().navigateUp() }
}

/**
 * 바코드 넘버로 비트맵 바코드 생성.
 *
 * @param widthPx
 * @param heightPx
 * @return Bitmap
 */
fun String.getBarcodeBitmap(widthPx: Int, heightPx: Int): Bitmap? {
    try {
        val hintMap: Hashtable<EncodeHintType, ErrorCorrectionLevel> = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        val codeWriter: Writer
        codeWriter = Code128Writer()
        val byteMatrix: BitMatrix = codeWriter.encode(this, BarcodeFormat.CODE_128, widthPx, heightPx, hintMap)
        val width = byteMatrix.width
        val height = byteMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap.setPixel(i, j, if (byteMatrix[i, j]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (exception: Exception) {
        exception.printStackTrace()
        return null
    }
}

/**
 * MARK: - Application
 *
 */
fun Application.goToIntroActivity() {
    val intent = Intent(this, IntroActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/**
 * MARK: - Activity
 *
 */
fun Activity.goToHomeActivity() {
    val intent = Intent(this, HomeActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Activity.goToIntroActivity() {
    val intent = Intent(this, IntroActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}


fun Activity.goToSystemSettingActivity() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
    overridePendingTransition(0, 0)
}