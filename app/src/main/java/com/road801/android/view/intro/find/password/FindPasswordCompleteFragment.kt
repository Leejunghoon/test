package com.road801.android.view.intro.find.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.road801.android.R
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.databinding.FragmentFindPasswordCompleteBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog.OnDialogListener
import com.road801.android.view.main.me.MeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 비밀번호 찾기  [ 완료 ]
 *
 */
@AndroidEntryPoint
class FindPasswordCompleteFragment : Fragment() {
    private lateinit var binding: FragmentFindPasswordCompleteBinding
    private val viewModel: MeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindPasswordCompleteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    private fun setupListener() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            showDialog(
                parentFragmentManager,
                title = "비밀번호 재설정 완료",
                "로그인 화면으로 이동합니다.",
                listener = object : OnDialogListener {
                    override fun onCancel() {
                    }

                    override fun onConfirm() {

                    }
                })
        }

        // 비밀번호
        binding.findPasswordCompletePasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                val pw = binding.findPasswordCompleteRePasswordEditText.text.toString().trim()
                validate(text, binding.findPasswordCompletePasswordTextInputLayout, text == pw)
                checkValidAndNextButtonEnabled(text == pw)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 비밀번호 재확인
        binding.findPasswordCompleteRePasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                val pw = binding.findPasswordCompleteRePasswordEditText.text.toString().trim()
                validate(text, binding.findPasswordCompleteRePasswordTextInputLayout, text == pw)
                checkValidAndNextButtonEnabled(text == pw)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun bindViewModel() {
        // 휴대폰 인증 요청
        viewModel.isRequestCert.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
//                            startCertification()
                        }
                    }
                    is Resource.Failure -> {
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }

        // 휴대폰 인증 완료
        viewModel.isCompleteCert.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if(it.data) {
                            checkValidAndNextButtonEnabled(true)
//                            endCertification()
                        }
                    }
                    is Resource.Failure -> {
//                        notValidCertification()
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }
    }

    private fun validate(text: String, inputLayout: TextInputLayout, equal: Boolean = true): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
            binding.findPasswordCompletePasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }

            binding.findPasswordCompleteRePasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }
        }

        if (text.isEmpty()) {
            inputLayout.error = emptyMessage
            return false
        }

        if (!isValid) {
            inputLayout.error = validMessage
            return false
        }

        if (!equal) {
            inputLayout.error = getString(R.string.input_valid_not_equal_pw)
            return false
        }

        inputLayout.error = null
        inputLayout.isErrorEnabled = false
        inputLayout.helperText = " "
        return true
    }

    private fun checkValidAndNextButtonEnabled(isEqual: Boolean) {
        binding.nextButton.isEnabled =
            !binding.findPasswordCompletePasswordTextInputLayout.isErrorEnabled &&
                    !binding.findPasswordCompleteRePasswordTextInputLayout.isErrorEnabled &&
                    isEqual
    }
}