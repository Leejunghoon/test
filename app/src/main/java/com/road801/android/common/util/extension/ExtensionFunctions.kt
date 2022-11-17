package com.road801.android.common.util.extension;

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.road801.android.BuildConfig
import com.road801.android.R
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.intro.IntroActivity
import com.road801.android.view.main.home.HomeActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * MARK: - 공통
 */
val Any.TAG: String get() = this::class.java.simpleName


@BindingAdapter("navigateUp")
fun bindNavigateUp(toolbar: Toolbar, listener: View.OnClickListener) {
    toolbar.setNavigationOnClickListener { toolbar.findNavController().navigateUp() }
}


fun String.formatted(pattern: String): String {
    val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    val date = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).parse(this)
    val dateFormat = SimpleDateFormat(pattern, Locale.KOREA)
    date?.let {
        return dateFormat.format(it)
    }
    return "Failed convert date to string"
}

val Int.currency: String get() = DecimalFormat("#,###").format(this)


fun BottomNavigationView.setupWithNavControllerFixed(navController: NavController?) {
    navController?.let {
        this.setupWithNavController(it)
    }


    /**
     *  Fixed by
     *     NavOptions setRestoreState(false) with  setPopUpTo(saveState = true)
     *     or
     *     NavOptions setRestoreState(true) with  setPopUpTo(saveState = false)
     *
     *  @see [NavigationUI.onNavDestinationSelected]
     */
    this.setOnItemSelectedListener {item->

        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (
            navController!!.currentDestination!!.parent!!.findNode(item.itemId)
                    is ActivityNavigator.Destination
        ) {
//            builder.setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
//            builder.setEnterAnim(R.animator.nav_default_enter_anim)
//                .setExitAnim(R.animator.nav_default_exit_anim)
//                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = true,
                saveState = true
            )
        }
        val options = builder.build()
        return@setOnItemSelectedListener  try {
            // TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(item.itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}

/**
 * 넘버로 비트맵 바코드 생성.
 *
 * @param width
 * @param height
 * @return Bitmap
 */
fun String.getBarcodeBitmap(type: BarcodeFormat, width: Int, height: Int): Bitmap? {
    try {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(this, type, width, height)
    } catch (e: Exception) {
        if(BuildConfig.DEBUG) Log.d(TAG, "Failed getBarcodeBitmap")
    }
    return null
}


/**
 * 뷰 마진 설정
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
    overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out)
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
                        confirmButtonTitle: String? = getString(R.string.confirm),
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
 * 달력
 *
 * @param callback y, m, d
 */
fun Activity.showCalendar(callback: (String, String, String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR).minus(20)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(this,
        { view, y, m, d ->
            callback.invoke("$y","%02d".format(m.plus(1)),"%02d".format(d))
        }, year, month, day)

    datePickerDialog.show()
}

/**
 * MARK: - Fragment
 *
 */

fun Fragment.showDialog(fragmentManager: FragmentManager,
                                      title: String = "[ 알림 ]",
                                      message: String,
                                      cancelButtonTitle: String? = null,
                                      confirmButtonTitle: String? = getString(R.string.confirm),
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
 * 달력
 *
 * @param callback y, m, d
 */
fun Fragment.showCalendar(callback: (String, String, String) -> Unit) {
    view?.let { activity?.showCalendar(callback) }
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}