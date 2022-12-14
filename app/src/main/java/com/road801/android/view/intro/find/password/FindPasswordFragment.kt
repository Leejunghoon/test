package com.road801.android.view.intro.find.password

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.road801.android.R
import com.road801.android.common.enum.SignupType
import com.road801.android.common.util.extension.hideKeyboard
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.extension.showToast
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.databinding.FragmentFindPasswordBinding
import com.road801.android.databinding.FragmentHomeBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.intro.IntroViewModel
import com.road801.android.view.intro.find.id.FindIdFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * MARK: - 비밀번호 재설정
 *
 */
class FindPasswordFragment : Fragment() {

    private lateinit var binding: FragmentFindPasswordBinding
    private val viewModel: IntroViewModel by activityViewModels()

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
        binding = FragmentFindPasswordBinding.inflate(inflater, container, false)
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
//        binding.closeButton.setOnClickListener {
//            findNavController().navigateUp()
//        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(FindPasswordFragmentDirections.actionFindPasswordFragmentToLoginFragment())
        }

        binding.nextButton.setOnClickListener {
            val mobileNo = binding.findPasswordPhoneEditText.text.toString().trim()
            val authValue = binding.findPasswordCertEditText.text.toString().trim()
            val password = binding.findPasswordConfirmPasswordEditText.text.toString().trim()
            viewModel.requestChangePassword(mobileNo, authValue, password)
        }


        // 번호 인증 요청
        binding.findPasswordRequestCertButton.setOnClickListener {
            val mobileNo = binding.findPasswordPhoneEditText.text.toString().trim()
            viewModel.requestChangePasswordAuth(mobileNo)
            hideKeyboard()
        }

//        // 번호 인증 확인
//        binding.findPasswordConfirmCertButton.setOnClickListener {
//            val mobileNo = binding.findPasswordPhoneEditText.text.toString().trim()
//            val authValue = binding.findPasswordCertEditText.text.toString().trim()
//
//            hideKeyboard()
//        }

        // 전화번호
        binding.findPasswordPhoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.findPasswordRequestCertButton.isEnabled =
                    validate(text, binding.findPasswordPhoneTextInputLayout)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 인증번호
        binding.findPasswordCertEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.findPasswordCertTextInputLayout)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // 비밀번호
        binding.findPasswordPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.findPasswordPasswordTextInputLayout, true)
                checkValidAndNextButtonEnabled(isEqualPassword())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 비밀번호 재확인
        binding.findPasswordConfirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.findPasswordConfirmPasswordTextInputLayout, isEqualPassword())
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
                            findNavController().navigate(FindPasswordFragmentDirections.actionFindPasswordFragmentToLoginFragment())
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
        val pw = binding.findPasswordPasswordEditText.text.toString().trim()
        val rePw = binding.findPasswordConfirmPasswordEditText.text.toString().trim()
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
            binding.findPasswordPhoneTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_phone)
                validMessage = getString(R.string.input_valid_phone)
                isValid = RoadValidator.phone(text)
            }

            binding.findPasswordCertTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_cert)
                validMessage = getString(R.string.input_valid_cert)
                isValid = RoadValidator.certNum(text)
            }

            binding.findPasswordPasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }

            binding.findPasswordConfirmPasswordTextInputLayout -> {
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
        binding.nextButton.isEnabled = !binding.findPasswordPhoneTextInputLayout.isErrorEnabled &&
                !binding.findPasswordCertTextInputLayout.isErrorEnabled &&
                !binding.findPasswordPasswordTextInputLayout.isErrorEnabled &&
                !binding.findPasswordConfirmPasswordTextInputLayout.isErrorEnabled &&
                isEqual

    }

    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@FindPasswordFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
                    binding.findPasswordRequestCertButton.text =
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
        binding.findPasswordPhoneCertContainer.visibility = View.VISIBLE // 인증번호 필드 VISIBLE
        binding.findPasswordRequestCertButton.isEnabled = false // 요청버튼 비활성화
        binding.findPasswordCertEditText.text = null            // 인증번호 초기화
        startCountDown()
    }

    // 유효하지 않은 인증번호
    private fun notValidCertification() {
        viewModel.resetCert()
    }

    // 인증번호 검증 완료 및 시간 만료
    private fun endCertification() {
        binding.findPasswordPhoneCertContainer.visibility = View.GONE   // 인증번호 필드 GONE
        binding.findPasswordRequestCertButton.text = "인증요청"           // 요청버튼 텍스트 초기화
        binding.findPasswordRequestCertButton.isEnabled = true          // 요청버튼 활성화

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