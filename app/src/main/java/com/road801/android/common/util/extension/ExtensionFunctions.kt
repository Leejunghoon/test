package com.road801.android.common.util.extension;

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.Writer
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.home.HomeActivity
import com.road801.android.view.intro.IntroActivity
import java.util.*

/**
 * MARK: - 공통
 */
val Any.TAG: String get() = this::class.java.simpleName

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
 * 코드로 뷰 마진 설정
 *
 * @param left
 * @param top
 * @param right
 * @param bottom
 */
fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}
inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

/**
 * MARK: - Application
 *
 */
fun Application.goToIntro() {
    val intent = Intent(this, IntroActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/**
 * MARK: - Activity
 *
 */
fun Activity.goToHome() {
    val intent = Intent(this, HomeActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Activity.goToIntro() {
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

fun Activity.showDialog(fragmentManager: FragmentManager,
                        title: String = "[ 알림 ]",
                        message: String,
                        cancelButtonTitle: String? = null,
                        confirmButtonTitle: String? = null,
                        listener: RoadDialog.OnDialogListener? = null) {
    val dialog = RoadDialog()
    dialog.title = title
    dialog.message = message
    dialog.cancelButtonTitle = cancelButtonTitle
    dialog.confirmButtonTitle = confirmButtonTitle
    dialog.onClickListener = listener
    dialog.show(fragmentManager, "showDialog")
}

/**
 * MARK: - Fragment
 *
 */

fun Fragment.showDialog(fragmentManager: FragmentManager,
                                      title: String = "[ 알림 ]",
                                      message: String,
                                      cancelButtonTitle: String? = null,
                                      confirmButtonTitle: String? = null,
                                      listener: RoadDialog.OnDialogListener? = null) {
    val dialog = RoadDialog()
    dialog.title = title
    dialog.message = message
    dialog.cancelButtonTitle = cancelButtonTitle
    dialog.confirmButtonTitle = confirmButtonTitle
    dialog.onClickListener = listener
    dialog.show(fragmentManager, "showDialog")
}