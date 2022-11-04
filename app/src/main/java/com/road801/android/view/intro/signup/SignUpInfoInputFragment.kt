package com.road801.android.view.intro.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.road801.android.BuildConfig
import com.road801.android.common.TAG
import com.road801.android.common.enum.SnsType
import com.road801.android.common.margin
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        setupListener()
    }

    private fun initView() {
        // 성별 기본값: 남자
        binding.signupSegmentRadioGroup.check(binding.signupSegmentGenderM.id)

        // 로드801 가입인지 SNS 가입인지 여부에 따라 화면 변경.
        val snsId = (viewModel.signupUser.value?.peekContent() as Resource.Success<UserDto>).data.socialId
        if (!snsId.isNullOrEmpty()) {
            binding.signupInfoIdContainer.visibility = View.GONE
            binding.signupInfoPasswordTextInputLayout.visibility = View.GONE
            binding.signupInfoNameTextInputLayout.margin(top = 40f)
        }

    }

    private fun setupListener() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

    }


}