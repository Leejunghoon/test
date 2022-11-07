package com.road801.android.view.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.road801.android.BuildConfig
import com.road801.android.common.enum.GenderType
import com.road801.android.common.enum.SnsType
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.showDialog
import com.road801.android.data.network.dto.UserDto
import com.road801.android.databinding.FragmentIntroBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding
    private val viewModel: IntroViewModel by activityViewModels()

    private lateinit var googleResultLauncher: ActivityResultLauncher<Intent> // for google login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForActivityResult()

        initView()
        bindViewModel()
    }

    private fun initView() {
        // 카카오 로그인
        binding.introSnsKakaoButton.setOnClickListener {
            viewModel.requestSnsLogin(requireContext(), SnsType.KAKAO /*,kakaoResultLauncher */)
        }
        // 네이버 로그인
        binding.introSnsNaverButton.setOnClickListener {
            viewModel.requestSnsLogin(requireContext(), SnsType.NAVER)
        }
        // 구글 로그인
        binding.introSnsGoogleButton.setOnClickListener {
            viewModel.requestSnsLogin(requireContext(), SnsType.GOOGLE, googleResultLauncher)
        }
        // 로드801 로그인 및 회원가입
        binding.introGoLoginButton.setOnClickListener{
            findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToLoginFragment())
        }
    }

    private fun bindViewModel() {
        viewModel.signupUser.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        findNavController().navigate(IntroFragmentDirections.actionIntroFragmentToSignUpTermsFragment())
                    }
                    is Resource.Failure -> { if(BuildConfig.DEBUG) Log.e(TAG, it.exception.domainErrorMessage) }
                }
            }
        }
    }

    // google login callback
    // registerForActivityResult 생성을 라이프사이클 내에서 선언해야함.
    private fun registerForActivityResult() {
        googleResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)

                    if(BuildConfig.DEBUG) Log.d(TAG, "account.id ${account.id}")

                    val userDto = UserDto(
                        name = "",
                        birthday = "",
                        mobileNo = "",
                        sexType =  GenderType.NONE,
                        termAgreeList = emptyList(),
                        socialType = SnsType.GOOGLE.name,
                        socialId = account.id,
                        loginId = null,
                        password = null,
                        thumbnailImageUrl = null
                    )

                    viewModel.setSignupUser(userDto = userDto)

                } catch (e: ApiException) {
                    if(BuildConfig.DEBUG) Log.e(TAG, "[구글] 로그인 실패")
                    viewModel.setSignupUser(error = e)
                }
            }
        }
    }


}