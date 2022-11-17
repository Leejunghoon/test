package com.road801.android.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.road801.android.R
import com.road801.android.databinding.DialogEventBinding


/**
 * MARK: - 회원가입 완료 및 이벤트 팝업
 *
 */
class EventDialog : DialogFragment() {

    interface OnDialogListener {
        fun onConfirm()
    }

    var title = ""
    var message = ""
    var confirmButtonTitle: String? = null
    var onClickListener: OnDialogListener? = null
    private var _binding: DialogEventBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_App_Dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
//        dialog.setOnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//                onClickListener?.onCancel()
//            }
//            return@setOnKeyListener false
//        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogFullConfirmButton.text = confirmButtonTitle
        binding.dialogFullConfirmButton.setOnClickListener {
            onClickListener?.onConfirm()
            dismissAllowingStateLoss()
        }
    }
}