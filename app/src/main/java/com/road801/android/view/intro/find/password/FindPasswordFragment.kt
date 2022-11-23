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
 * MARK: - 비밀번호 찾기  [ 인증 ]
 *
 */
class FindPasswordFragment : Fragment() {

    private lateinit var binding: FragmentFindPasswordBinding
    private val viewModel: IntroViewModel by activityViewModels()

    private lateinit var countDownTimer: CountDownTimer
    private val MAX_MINUTE = 1L
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
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(FindPasswordFragmentDirections.actionFindPasswordFragmentToFindPasswordCompleteFragment())
        }


        // 번호 인증 요청
        binding.findPasswordRequestCertButton.setOnClickListener {
            val spendTime = MAX_MINUTE * 60 - remainTimeSeconds
            if (spendTime < 60) {
                Snackbar.make(it, "$spendTime 초 후에 다시 시도해주세요.", 1000).show()
                return@setOnClickListener
            }
            val mobileNo = binding.findPasswordPhoneEditText.text.toString().trim()
            viewModel.requestPhoneAuth(PhoneAuthRequestDto(mobileNo = mobileNo, authValue = ""))
        }

        // 번호 인증 확인
        binding.findPasswordConfirmCertButton.setOnClickListener {
            val mobileNo = binding.findPasswordPhoneEditText.text.toString().trim()
            val authValue = binding.findPasswordCertEditText.text.toString().trim()
            viewModel.requestPhoneAuthConfirm(mobileNo, authValue)
            hideKeyboard()
        }

        // 전화번호
        binding.findPasswordPhoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.findPasswordRequestCertButton.isEnabled =
                    validate(text, binding.findPasswordPhoneTextInputLayout)
                checkValidAndNextButtonEnabled()
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
                binding.findPasswordConfirmCertButton.isEnabled =
                    validate(text, binding.findPasswordCertTextInputLayout)
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
                            checkValidAndNextButtonEnabled()
                            endCertification()
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


    private fun validate(text: String, inputLayout: TextInputLayout): Boolean {
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
        val isCertComplete = viewModel.isCertComplete()
        binding.nextButton.isEnabled = !binding.findPasswordPhoneTextInputLayout.isErrorEnabled
                && !binding.findPasswordCertTextInputLayout.isErrorEnabled
                && isCertComplete
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
                    binding.findPasswordRequestCertButton.text = getString(R.string.valid_time, minutes, seconds)
                }
            }

            override fun onFinish() {
                endCertification()
            }
        }
    }


    // 인증번호 검증 시작
    private fun startCertification() {
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