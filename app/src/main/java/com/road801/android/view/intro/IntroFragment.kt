package com.road801.android.view.intro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.road801.android.common.enum.SnsType
import com.road801.android.databinding.FragmentIntroBinding
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
            Log.d("intro", "introSnsKakaoButton")
            viewModel.requestSnsLogin(requireContext(), SnsType.KAKAO)
        }
        binding.introSnsNaverButton.setOnClickListener {
            Log.d("intro", "introSnsNaverButton")
        }
        binding.introSnsGoogleButton.setOnClickListener {
            Log.d("intro", "introSnsGoogleButton")
        }

    }

    private fun bindViewModel() {
        viewModel.signupUser.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {Log.d(tag, it.data.toString())}
                    is Resource.Failure -> {Log.e(tag, it.exception.domainErrorMessage)}
                }
            }
        }
    }

}