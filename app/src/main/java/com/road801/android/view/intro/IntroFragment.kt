package com.road801.android.view.intro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.road801.android.BuildConfig
import com.road801.android.common.TAG
import com.road801.android.common.enum.SnsType
import com.road801.android.data.repository.SnsRepository
import com.road801.android.databinding.FragmentIntroBinding
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource

class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding
    private val viewModel: IntroViewModel by viewModels()

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
        initView()
        bindViewModel()
    }

    private fun initView() {

        binding.introSnsKakaoButton.setOnClickListener {
            if(BuildConfig.DEBUG) Log.d(TAG, "introSnsKakaoButton")
            viewModel.requestSnsLogin(requireContext(), SnsType.KAKAO)
        }
        binding.introSnsNaverButton.setOnClickListener {
            if(BuildConfig.DEBUG) Log.d(TAG, "introSnsNaverButton")
            viewModel.requestSnsLogin(requireContext(), SnsType.NAVER)
        }
        binding.introSnsGoogleButton.setOnClickListener {
            if(BuildConfig.DEBUG) Log.d(TAG, "introSnsGoogleButton")
        }

    }

    private fun bindViewModel() {
        viewModel.signupUser.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> { if(BuildConfig.DEBUG) Log.d(TAG, it.data.toString()) }
                    is Resource.Failure -> { if(BuildConfig.DEBUG) Log.e(TAG, it.exception.domainErrorMessage) }
                }
            }
        }
    }

}