package com.road801.android.view.intro.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.road801.android.common.enum.SnsType
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.margin
import com.road801.android.data.network.dto.UserDto
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpInfoInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        setupListener()
    }

    private fun initView() {
        // 버튼 초기 셋팅
        binding.signupIdDuplicateButton.isEnabled = false
        binding.signupPhoneAuthButton.isEnabled = false
        binding.signupPhoneCertButton.isEnabled = false

        // 성별 기본값: 여자
        binding.signupSegmentRadioGroup.check(binding.signupSegmentGenderF.id)

        // 로드801 가입인지 SNS 가입인지 여부에 따라 필드 종류 변경.
        val snsId = (viewModel.signupUser.value?.peekContent() as Resource.Success<UserDto>).data.socialId
        if (!snsId.isNullOrEmpty()) {
            binding.signupInfoIdContainer.visibility = View.GONE
            binding.signupInfoPasswordTextInputLayout.visibility = View.GONE
            binding.signupInfoNameTextInputLayout.margin(top = 40f)
        }

    }

    private fun bindViewModel() {
        viewModel.memberExist.observe(viewLifecycleOwner) { result ->
            result.peekContent().let {
                Log.d(TAG, it.toString())
            }
        }
    }

    private fun setupListener() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
            viewModel.requestIsIdExist("ddd", SnsType.KAKAO)
        }
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.nextButton.setOnClickListener {
            findNavController().navigate(SignUpInfoInputFragmentDirections.actionSignUpInfoInputFragmentToSignUpCompleteFragment())
        }

    }


}