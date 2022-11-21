package com.road801.android.view.main.me.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.kakao.sdk.user.model.Gender
import com.road801.android.R
import com.road801.android.common.enum.GenderType
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.data.network.dto.MeDto
import com.road801.android.databinding.FragmentProfileBinding
import com.road801.android.domain.model.SettingType
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.intro.signup.SignUpInfoInputFragmentArgs
import com.road801.android.view.main.me.MeFragmentDirections
import com.road801.android.view.main.me.MeViewModel
import com.road801.android.view.main.me.adapter.MeRecyclerAdapter
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
        // 버튼 초기 셋팅
        binding.profileConfirmCertButton.isEnabled = false

    }

    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.profilePasswordChangeButton.setOnClickListener {

        }
    }

    private fun bindMe() {
        binding.profileNameEditText.setText(args.meDto.name)
        binding.profileBirthEditText.setText(args.meDto.name)
//        binding.profilePhoneEditText.setText(meDto.phone)

        if (GenderType.valueOf(args.meDto.sexType.code) == GenderType.FEMALE){
            binding.profileSegmentRadioGroup.check(binding.profileSegmentGenderF.id)
        } else {
            binding.profileSegmentRadioGroup.check(binding.profileSegmentGenderM.id)
        }

    }

    private fun bindViewModel() {
        viewModel.meInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

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
    }



    private fun validate(text: String, inputLayout: TextInputLayout): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
//            binding.profileIdTextInputLayout -> {
//                emptyMessage = getString(R.string.input_empty_id)
//                validMessage = getString(R.string.input_valid_id)
//                isValid = RoadValidator.id(text)
//            }
//
//            binding.profilePasswordTextInputLayout -> {
//                emptyMessage = getString(R.string.input_empty_pw)
//                validMessage = getString(R.string.input_valid_pw)
//                isValid = RoadValidator.password(text)
//            }

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

    private fun setupCountDownTimer(interval: Long) {
        countDownTimer = object : CountDownTimer(MAX_VALID_TIME, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60 % 60
                val seconds = (millisUntilFinished / 1000) % 60
                synchronized(this@ProfileFragment) {
                    remainTimeSeconds = (minutes * 60) + seconds
                }
                activity?.runOnUiThread {
//                    binding.profileRequestCertButton.text = getString(R.string.valid_time, minutes, seconds)
                }
            }

            override fun onFinish() {
//                binding.profileRequestCertButton.text = "인증요청"
//                binding.profileRequestCertButton.isEnabled = true
            }
        }
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