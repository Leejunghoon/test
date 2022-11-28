package com.road801.android.view.main.me.change.phone

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
import com.road801.android.databinding.FragmentChangePhoneBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.me.MeViewModel

/**
 * MARK: - 휴대폰번호 변경
 *
 */
class ChangePhoneFragment : Fragment() {

    private lateinit var binding: FragmentChangePhoneBinding
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
        binding = FragmentChangePhoneBinding.inflate(inflater, container, false)
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
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        // 휴대폰번호 변경 버튼
        binding.nextButton.setOnClickListener {
            val mobileNo = binding.changePhoneEditText.text.toString().trim()
            val authValue = binding.changePhoneCertEditText.text.toString().trim()
            viewModel.requestPhoneAuthConfirm(mobileNo, authValue)
        }


        // 번호 인증 요청
        binding.changePhoneRequestCertButton.setOnClickListener {
            val mobileNo = binding.changePhoneEditText.text.toString().trim()
            viewModel.requestPhoneAuth(mobileNo)
            hideKeyboard()
        }

        // 전화번호
        binding.changePhoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.changePhoneRequestCertButton.isEnabled =
                    validate(text, binding.changePhoneTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 인증번호
        binding.changePhoneCertEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.changePhoneCertTextInputLayout)
                checkValidAndNextButtonEnabled()
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
        viewModel.isCompleteCert.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            stopCountDown()
                            showToast("휴대폰번호가 변경되었습니다.")
                            findNavController().popBackStack()
                        }
                    }
                    is Resource.Failure -> {
                        notValidCertification()
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


    private fun validate(
        text: String,
        inputLayout: TextInputLayout
    ): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
            binding.changePhoneTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_phone)
                validMessage = getString(R.string.input_valid_phone)
                isValid = RoadValidator.phone(text)
            }

            binding.changePhoneCertTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_cert)
                validMessage = getString(R.string.input_valid_cert)
                isValid = RoadValidator.certNum(text)
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

        inputLayout.error = null
        inputLayout.isErrorEnabled = false
        inputLayout.helperText = " "
        return true
    }


    private fun checkValidAndNextButtonEnabled() {
        binding.nextButton.isEnabled =  !binding.changePhoneTextInputLayout.isErrorEnabled &&
                !binding.changePhoneCertTextInputLayout.isErrorEnabled

    }

    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@ChangePhoneFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
                    binding.changePhoneRequestCertButton.text =
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
        binding.changePhoneCertContainer.visibility = View.VISIBLE // 인증번호 필드 VISIBLE
        binding.changePhoneRequestCertButton.isEnabled = false // 요청버튼 비활성화
        binding.changePhoneCertEditText.text = null            // 인증번호 초기화
        startCountDown()
    }

    // 유효하지 않은 인증번호
    private fun notValidCertification() {
        viewModel.resetCert()
    }

    // 인증번호 검증 완료 및 시간 만료
    private fun endCertification() {
        binding.changePhoneCertContainer.visibility = View.GONE   // 인증번호 필드 GONE
        binding.changePhoneRequestCertButton.text = "인증요청"           // 요청버튼 텍스트 초기화
        binding.changePhoneRequestCertButton.isEnabled = true          // 요청버튼 활성화

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