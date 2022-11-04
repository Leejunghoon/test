package com.road801.android.view.intro.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.road801.android.databinding.FragmentSignUpCompleteBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 회원가입  [ 완료 ]
 *
 */
@AndroidEntryPoint
class SignUpCompleteFragment : Fragment() {

    private lateinit var binding: FragmentSignUpCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCompleteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}