package com.road801.android.view.intro.signup

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.road801.android.R
import com.road801.android.common.enum.GenderType
import com.road801.android.common.enum.SignupType
import com.road801.android.common.util.extension.hideKeyboard
import com.road801.android.common.util.extension.showCalendar
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.data.network.dto.UserDto
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.data.network.dto.requset.SignupRequestDto
import com.road801.android.databinding.FragmentSignUpInfoInputBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.intro.IntroViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 회원가입  [회원 정보 추가 입력]
 *
 */

@AndroidEntryPoint
class SignUpInfoInputFragment : Fragment() {
    private lateinit var binding: FragmentSignUpInfoInputBinding
    private val viewModel: IntroViewModel by activityViewModels()
    private val args: SignUpInfoInputFragmentArgs by navArgs()
    private lateinit var signupRequestDto: SignupRequestDto

    private lateinit var countDownTimer: CountDownTimer
    private val MAX_MINUTE = 1L
    private val MAX_VALID_TIME: Long = MAX_MINUTE * 60000 // MAX_MINUTE to millisecond
    private var remainTimeSeconds = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpInfoInputBinding.inflate(inflater, container, false)
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
        // 로드801 가입인지 SNS 가입인지 여부에 따라 필드 종류 변경.
        if (args.signupType == SignupType.SNS) {
            binding.signupInfoPasswordTextInputLayout.visibility = View.GONE
        }
        binding.signupInfoSegmentRadioGroup.check(binding.signupInfoSegmentGenderF.id)

        val signupUser = (viewModel.signupUser.value?.peekContent() as? Resource.Success<*>)?.data as? UserDto
        signupUser?.let { user ->

            // 이름
            user.name?.let {
                binding.signupInfoNameEditText.setText(it)
            }

            // 휴대폰 번호
            user.mobileNo?.let {
                binding.signupInfoPhoneEditText.setText(it)
            }

            // 생년월일
            user.birthday?.let {
                binding.signupInfoBirthEditText.setText(it)
            }

            // 성별
            user.sexType?.let {
                when (it) {
                    GenderType.MALE -> {
                        binding.signupInfoSegmentRadioGroup.check(binding.signupInfoSegmentGenderM.id)
                    }
                    GenderType.FEMALE, GenderType.NONE -> {
                        binding.signupInfoSegmentRadioGroup.check(binding.signupInfoSegmentGenderF.id)
                    }
                }
            }
        }
    }

    private fun bindViewModel() {
        // 이미 가입된 회원 여부
        viewModel.isMemberExist.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            showDialog(parentFragmentManager, message = "이미 존재하는 아이디 입니다.")
                        } else {

                        }
                    }
                    is Resource.Failure -> {
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }

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
                            checkValidAndNextButtonEnabled()
                            endCertification()
                        }
                    }
                    is Resource.Failure -> {
                        notValidCertification()
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }


        // 회원가입 성공 여부
        viewModel.isSuccessSignup.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data) {
                            findNavController().navigate(SignUpInfoInputFragmentDirections.actionSignUpInfoInputFragmentToSignUpCompleteFragment(args.signupType, signupRequestDto))
                        }
                    }
                    is Resource.Failure -> {
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }
    }

    private fun setupListener() {

        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.nextButton.setOnClickListener {
            val name = binding.signupInfoNameEditText.text.toString().trim()
            val birthday = binding.signupInfoBirthEditText.text.toString().trim()
            val mobileNo = binding.signupInfoPhoneEditText.text.toString().trim() // equal loginId
            val termAgreeList = args.termsList.toCollection(ArrayList<String>())
            val gender = if (binding.signupInfoSegmentRadioGroup.checkedButtonId == binding.signupInfoSegmentGenderM.id) {
                 GenderType.MALE.name
            } else {
                GenderType.FEMALE.name
            }

            var snsId: String? = null
            var snsType: String? = null
            var password: String? = null

            if (args.signupType == SignupType.SNS) {
                viewModel.getUser()?.let {
                    snsId =  it.socialId!!
                    snsType = it.socialType!!
                }
            } else {
                password = binding.signupInfoPasswordEditText.text.toString().trim()
            }

            this.signupRequestDto = SignupRequestDto(
                name = name,
                birthday = birthday,
                mobileNo = mobileNo,
                sexType = gender,
                termAgreeList = termAgreeList,
                socialType = snsType,
                socialId = snsId,
                loginId = mobileNo,
                password = password
            )
            viewModel.requestSignup(signupRequestDto)
        }

//        binding.signupIdDuplicateButton.setOnClickListener {
//            val id = binding.signupInfoIdEditText.text
//            if (!id.isNullOrEmpty()) {
//                viewModel.requestIsIdExist(id.toString().trim())
//            }
//        }

        // 번호 인증 요청
        binding.signupInfoRequestCertButton.setOnClickListener {
            val mobileNo = binding.signupInfoPhoneEditText.text.toString().trim()
            viewModel.requestPhoneAuth(PhoneAuthRequestDto(mobileNo = mobileNo, authValue = ""))
        }

        // 번호 인증 확인
        binding.signupInfoConfirmCertButton.setOnClickListener {
            val mobileNo = binding.signupInfoPhoneEditText.text.toString().trim()
            val authValue = binding.signupInfoCertEditText.text.toString().trim()
            viewModel.requestPhoneAuthConfirm(mobileNo, authValue)
            hideKeyboard()
        }

        // 캘린더
        binding.signupInfoCalendarButton.setOnClickListener {
            showCalendar { y, m, d ->
                binding.signupInfoBirthEditText.setText("$y-$m-$d")
                checkValidAndNextButtonEnabled()
            }
        }

        // 비밀번호
        binding.signupInfoPasswordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.signupInfoPasswordTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 이름
        binding.signupInfoNameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.signupInfoNameTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 전화번호
        binding.signupInfoPhoneEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.signupInfoRequestCertButton.isEnabled = validate(text, binding.signupInfoPhoneTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 인증번호
        binding.signupInfoCertEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.signupInfoConfirmCertButton.isEnabled = validate(text, binding.signupInfoCertTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun validate(text: String, inputLayout: TextInputLayout): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
            binding.signupInfoIdTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_id)
                validMessage = getString(R.string.input_valid_id)
                isValid = RoadValidator.id(text)
            }

            binding.signupInfoPasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }

            binding.signupInfoNameTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_name)
                validMessage = getString(R.string.input_valid_name)
                isValid = RoadValidator.name(text)
            }

            binding.signupInfoPhoneTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_phone)
                validMessage = getString(R.string.input_valid_phone)
                isValid = RoadValidator.phone(text)
            }

            binding.signupInfoCertTextInputLayout -> {
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
        val isCompleteCert = viewModel.isCertComplete()

        if (args.signupType == SignupType.SNS) {
            binding.nextButton.isEnabled =
                !binding.signupInfoNameTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoPhoneTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoCertTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoBirthEditText.text.isNullOrEmpty() && isCompleteCert
        } else {
            binding.nextButton.isEnabled =
                !binding.signupInfoPasswordTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoNameTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoPhoneTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoCertTextInputLayout.isErrorEnabled &&
                        !binding.signupInfoBirthEditText.text.isNullOrEmpty() && isCompleteCert
        }

    }


    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@SignUpInfoInputFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
                    binding.signupInfoRequestCertButton.text = getString(R.string.valid_time, minutes, seconds)
                }
            }

            override fun onFinish() {
                endCertification()
            }
        }
    }

    // 인증번호 검증 시작
    private fun startCertification() {
        binding.signupInfoPhoneCertContainer.visibility = View.VISIBLE // 인증번호 필드 VISIBLE
        binding.signupInfoRequestCertButton.isEnabled = false // 요청버튼 비활성화
        binding.signupInfoCertEditText.text = null            // 인증번호 초기화
        startCountDown()
    }

    // 유효하지 않은 인증번호
    private fun notValidCertification() {
        viewModel.resetCert()
    }

    // 인증번호 검증 완료 및 시간 만료
    private fun endCertification() {
        binding.signupInfoPhoneCertContainer.visibility = View.GONE   // 인증번호 필드 GONE
        binding.signupInfoRequestCertButton.text = "인증요청"           // 요청버튼 텍스트 초기화
        binding.signupInfoRequestCertButton.isEnabled = true          // 요청버튼 활성화
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

    private fun resetCert() {
        viewModel.resetCert()

        binding.signupInfoCertEditText.text = null
        binding.signupInfoConfirmCertButton.isEnabled = false
    }
}