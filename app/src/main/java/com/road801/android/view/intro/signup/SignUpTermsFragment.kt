package com.road801.android.view.intro.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.road801.android.databinding.FragmentSignUpTermsBinding
import com.road801.android.view.intro.IntroFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 회원가입  [ 이용 약관 동의 ]
 *
 */

@AndroidEntryPoint
class SignUpTermsFragment : Fragment() {

    private lateinit var binding: FragmentSignUpTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpTermsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
//        findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToSignUpInfoInputFragment())
        return binding.root
    }

}