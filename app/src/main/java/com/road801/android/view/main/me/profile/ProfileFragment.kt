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

        bindViewModel()
    }


    private fun initView() {
        
    }

    private fun bindMe() {
        binding.profileNameEditText.setText(args.meDto.name)
        binding.profileBirthEditText.setText(args.meDto.birthday)

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
            binding.profilePasswordChangeButton.visibility = View.INVISIBLE
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

        // 휴대폰번호 재설정
        binding.profilePhoneChangeButton.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangePhoneFragment())
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

        // 프로필 사진 수정 완료
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