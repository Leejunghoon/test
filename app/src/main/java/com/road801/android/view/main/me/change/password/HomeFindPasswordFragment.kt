package com.road801.android.view.main.me.change.password

import android.os.Bundle
import android.os.CountDownTimer
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
import com.road801.android.common.util.extension.hideKeyboard
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.extension.showToast
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.databinding.FragmentHomeFindPasswordBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.me.MeViewModel

/**
 * MARK: - 비밀번호 재설정
 *
 */
class HomeFindPasswordFragment : Fragment() {

    private lateinit var binding: FragmentHomeFindPasswordBinding
    private val viewModel: MeViewModel by viewModels()

    private lateinit var countDownTimer: CountDownTimer
    private val MAX_MINUTE = 3L
    private val MAX_VALID_TIME: Long = MAX_MINUTE * 60000 // MAX_MINUTE to millisecond
    private var remainTimeSeconds = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeFindPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountDownTimer(1000)
        bindViewModel()

    }

    private fun initView() {
    }


    private fun setupListener() {

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 비밀번호 변경 버튼
        binding.nextButton.setOnClickListener {
            val mobileNo = binding.homeFindPasswordPhoneEditText.text.toString().trim()
            val authValue = binding.homeFindPasswordCertEditText.text.toString().trim()
            val password = binding.homeFindPasswordConfirmPasswordEditText.text.toString().trim()
            viewModel.requestChangePassword(mobileNo, authValue, password)
        }


        // 번호 인증 요청
        binding.homeFindPasswordRequestCertButton.setOnClickListener {
            val mobileNo = binding.homeFindPasswordPhoneEditText.text.toString().trim()
            viewModel.requestChangePasswordAuth(mobileNo)
            hideKeyboard()
        }

        // 전화번호
        binding.homeFindPasswordPhoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.homeFindPasswordRequestCertButton.isEnabled =
                    validate(text, binding.homeFindPasswordPhoneTextInputLayout)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 인증번호
        binding.homeFindPasswordCertEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.homeFindPasswordCertTextInputLayout)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // 비밀번호
        binding.homeFindPasswordPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.homeFindPasswordPasswordTextInputLayout, true)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 비밀번호 재확인
        binding.homeFindPasswordConfirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.homeFindPasswordConfirmPasswordTextInputLayout, isEqualPassword())
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }


    private fun bindViewModel() {
        // 휴대폰 인증 요청
        viewModel.isRequestPwCert.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            startCertification()
                        }
                    }
                    is Resource.Failure -> {
                        showDialog(
                            parentFragmentManager,
                            title = it.exception.domainErrorMessage,
                            message = it.exception.domainErrorSubMessage
                        )
                    }
                }
            }
        }

        // 휴대폰 인증 완료
        viewModel.isCompletePwCert.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            stopCountDown()
                            showToast("비밀번호가 변경 되었습니다.")
                            findNavController().popBackStack()
                        }
                    }
                    is Resource.Failure -> {
//                        notValidCertification()
                        showDialog(
                            parentFragmentManager,
                            title = it.exception.domainErrorMessage,
                            message = it.exception.domainErrorSubMessage
                        )
                    }
                }
            }
        }
    }

    private fun isEqualPassword(): Boolean {
        val pw = binding.homeFindPasswordPasswordEditText.text.toString().trim()
        val rePw = binding.homeFindPasswordConfirmPasswordEditText.text.toString().trim()
        return pw == rePw
    }


    private fun validate(
        text: String,
        inputLayout: TextInputLayout,
        equal: Boolean = true
    ): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
            binding.homeFindPasswordPhoneTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_phone)
                validMessage = getString(R.string.input_valid_phone)
                isValid = RoadValidator.phone(text)
            }

            binding.homeFindPasswordCertTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_cert)
                validMessage = getString(R.string.input_valid_cert)
                isValid = RoadValidator.certNum(text)
            }

            binding.homeFindPasswordPasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }

            binding.homeFindPasswordConfirmPasswordTextInputLayout -> {
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
        binding.nextButton.isEnabled = !binding.homeFindPasswordPhoneTextInputLayout.isErrorEnabled &&
                !binding.homeFindPasswordCertTextInputLayout.isErrorEnabled &&
                !binding.homeFindPasswordPasswordTextInputLayout.isErrorEnabled &&
                !binding.homeFindPasswordConfirmPasswordTextInputLayout.isErrorEnabled &&
                isEqual

    }

    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@HomeFindPasswordFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
                    binding.homeFindPasswordRequestCertButton.text =
                        getString(R.string.valid_time, minutes, seconds)
                }
            }

            override fun onFinish() {
                endCertification()
            }
        }
    }


    // 인증번호 검증 시작
    private fun startCertification() {
        binding.nextButton.isEnabled = false
        binding.homeFindPasswordPhoneCertContainer.visibility = View.VISIBLE // 인증번호 필드 VISIBLE
        binding.homeFindPasswordRequestCertButton.isEnabled = false // 요청버튼 비활성화
        binding.homeFindPasswordCertEditText.text = null            // 인증번호 초기화
        startCountDown()
    }

    // 유효하지 않은 인증번호
    private fun notValidCertification() {
        viewModel.resetCert()
    }

    // 인증번호 검증 완료 및 시간 만료
    private fun endCertification() {
        binding.homeFindPasswordPhoneCertContainer.visibility = View.GONE   // 인증번호 필드 GONE
        binding.homeFindPasswordRequestCertButton.text = "인증요청"           // 요청버튼 텍스트 초기화
        binding.homeFindPasswordRequestCertButton.isEnabled = true          // 요청버튼 활성화

        stopCountDown()
    }

    private fun startCountDown() {
        stopCountDown()
        countDownTimer.start()
    }

    private fun stopCountDown() {
        synchronized(this) {
            remainTimeSeconds = 0L
            countDownTimer.cancel()
        }
    }

}