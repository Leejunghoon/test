package com.road801.android.view.intro.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.road801.android.BuildConfig
import com.road801.android.common.enum.LoginType
import com.road801.android.common.enum.SignupType
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.goToHome
import com.road801.android.common.util.extension.showDialog
import com.road801.android.databinding.FragmentSignUpCompleteBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.intro.IntroViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 회원가입  [ 완료 ]
 *
 */
@AndroidEntryPoint
class SignUpCompleteFragment : Fragment() {

    private lateinit var binding: FragmentSignUpCompleteBinding
    private val viewModel: IntroViewModel by activityViewModels()
    private val args: SignUpCompleteFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable backPress
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCompleteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()

    }

    private fun bindViewModel() {
        // 로그인 성공 여부
        viewModel.isSuccessLogin.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (BuildConfig.DEBUG) Log.d(TAG, it.data.accessToken)
                        activity?.goToHome()
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

    private fun setListener() {
        binding.nextButton.setOnClickListener {
            if (args.signupType == SignupType.SNS) {
                viewModel.requestLogin()
            } else {
                val loginId = args.signupRequestDto.loginId!!
                val loginPw = args.signupRequestDto.password!!
                viewModel.requestLogin(LoginType.ROAD801, loginId,loginPw)
            }

        }
    }
}