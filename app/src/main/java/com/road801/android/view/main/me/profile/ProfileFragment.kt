package com.road801.android.view.main.me.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.textfield.TextInputLayout
import com.road801.android.R
import com.road801.android.common.enum.GenderType
import com.road801.android.common.enum.LoginType
import com.road801.android.common.util.extension.hideKeyboard
import com.road801.android.common.util.extension.showCalendar
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.extension.showToast
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.data.network.dto.requset.MeRequestDto
import com.road801.android.databinding.FragmentProfileBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.me.MeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MeViewModel by viewModels()
    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var choseImageActivityResult: ActivityResultLauncher<Intent>
    private lateinit var countDownTimer: CountDownTimer
    private val MAX_MINUTE = 1L
    private val MAX_VALID_TIME: Long = MAX_MINUTE * 60000 // MAX_MINUTE to millisecond
    private var remainTimeSeconds = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        bindMe()
        setListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountDownTimer(1000)
        bindViewModel()
    }


    private fun initView() {
        
    }

    private fun bindMe() {
        binding.profileNameEditText.setText(args.meDto.name)
        binding.profileBirthEditText.setText(args.meDto.birthday)
        binding.profilePhoneEditText.setText(args.meDto.mobileNo)

        if (GenderType.valueOf(args.meDto.sexType.code) == GenderType.FEMALE){
            binding.profileSegmentRadioGroup.check(binding.profileSegmentGenderF.id)
        } else {
            binding.profileSegmentRadioGroup.check(binding.profileSegmentGenderM.id)
        }

        args.meDto.profileImage?.let {
            Glide.with(requireContext())
                .load(it)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.profileImageButton)
        }

        if (LoginType.valueOf(args.meDto.signupType.code) != LoginType.DEFAULT) {
            binding.profilePasswordChangeButton.visibility = View.GONE
        }

    }

    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        // 수정하기
        binding.nextButton.setOnClickListener {
            showDialog(
                parentFragmentManager,
                title = "프로필 수정",
                "수정하신 내용이 반영되어\n프로필이 수정 됩니다.\n\n정말 수정하시겠습니까?",
                cancelButtonTitle = "수정하기",
                confirmButtonTitle = "돌아가기",
                listener = object : RoadDialog.OnDialogListener {
                    override fun onCancel() {
                        // 수정하기
                        val name = binding.profileNameEditText.text.toString().trim()
                        val birthday = binding.profileBirthEditText.text.toString().trim()
                        val gender = if (binding.profileSegmentRadioGroup.checkedButtonId == binding.profileSegmentGenderM.id) {
                            GenderType.MALE.name
                        } else {
                            GenderType.FEMALE.name
                        }
                        viewModel.modifyMe(MeRequestDto(name, birthday, gender))
                    }

                    override fun onConfirm() {
                       // 돌아가기
                    }
                }
            )
        }

        // 프로필 이미지 수정
        binding.profileImageButton.setOnClickListener {
            goToImageFileBrowser()
        }

        // 비밀번호 재설정 
        binding.profilePasswordChangeButton.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFindPasswordFragment())
        }

        // 이름
        binding.profileNameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.profileNameTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 전화번호
        binding.profilePhoneEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.profileRequestCertButton.isEnabled = validate(text, binding.profilePhoneTextInputLayout)
//                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 인증번호
        binding.profileCertEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                binding.profileConfirmCertButton.isEnabled = validate(text, binding.profileCertTextInputLayout)
//                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        // 번호 인증 요청
        binding.profileRequestCertButton.setOnClickListener {
            val mobileNo = binding.profilePhoneEditText.text.toString().trim()
            viewModel.requestPhoneAuth(mobileNo)
            hideKeyboard()
        }

        // 번호 인증 확인
        binding.profileConfirmCertButton.setOnClickListener {
            val mobileNo = binding.profilePhoneEditText.text.toString().trim()
            val authValue = binding.profileCertEditText.text.toString().trim()
            viewModel.requestPhoneAuthConfirm(mobileNo, authValue)
            hideKeyboard()
        }

        // 캘린더
        binding.profileCalendarButton.setOnClickListener {
            showCalendar { y, m, d ->
                binding.profileBirthEditText.setText("$y-$m-$d")
//                checkValidAndNextButtonEnabled()
            }
        }

    }

    /**
     * MARK: -------------------------- bindViewModel
     *
     */
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
                        // 인증 성공 했을 경우에만 토스트
                        if (it.data) {
//                        checkValidAndNextButtonEnabled()
                            endCertification()
                            showToast("휴대폰 번호가 변경되었습니다.")
                        }
                    }
                    is Resource.Failure -> {
                        notValidCertification()
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }

        // 프로필 수정 완료
        viewModel.isCompleteChange.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        showToast("프로필 정보가 수정되었습니다.")
                        findNavController().popBackStack()
                    }
                    is Resource.Failure -> {
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
                    }
                }
            }
        }

        // 프로필 수정 완료
        viewModel.uploadFileInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val uploadFile = it.data

                        Glide.with(requireContext())
                            .load(uploadFile.previewPath)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(binding.profileImageButton)

                        showToast("프로필 사진이 수정되었습니다.")
                    }
                    is Resource.Failure -> {
                        showDialog(parentFragmentManager, title = it.exception.domainErrorMessage, message = it.exception.domainErrorSubMessage)
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

            binding.profileNameTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_name)
                validMessage = getString(R.string.input_valid_name)
                isValid = RoadValidator.name(text)
            }

            binding.profilePhoneTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_phone)
                validMessage = getString(R.string.input_valid_phone)
                isValid = RoadValidator.phone(text)
            }

            binding.profileCertTextInputLayout -> {
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
        binding.nextButton.isEnabled = !binding.profileNameTextInputLayout.isErrorEnabled
    }

    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@ProfileFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
                    binding.profileRequestCertButton.text = getString(R.string.valid_time, minutes, seconds)
                }
            }

            override fun onFinish() {
                endCertification()
            }
        }
    }

    // 인증번호 검증 시작
    private fun startCertification() {
        binding.profilePhoneCertContainer.visibility = View.VISIBLE // 인증번호 필드 VISIBLE
        binding.profileRequestCertButton.isEnabled = false // 요청버튼 비활성화
        binding.profileCertEditText.text = null            // 인증번호 초기화
        startCountDown()
    }

    // 유효하지 않은 인증번호
    private fun notValidCertification() {
        viewModel.resetCert()
    }

    // 인증번호 검증 완료 및 시간 만료
    private fun endCertification() {
        binding.profilePhoneCertContainer.visibility = View.GONE   // 인증번호 필드 GONE
        binding.profileRequestCertButton.text = "인증요청"           // 요청버튼 텍스트 초기화
        binding.profileRequestCertButton.isEnabled = true          // 요청버튼 활성화

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


    private fun registerForActivityResult() {
        choseImageActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                result.data?.let { intent ->
                    intent.data?.let {
                        viewModel.uploadImageFile(it)
                    }
                }
            }
        }
    }

    private fun goToImageFileBrowser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            choseImageActivityResult.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}