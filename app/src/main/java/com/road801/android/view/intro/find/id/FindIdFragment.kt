package com.road801.android.view.intro.find.id

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.road801.android.common.enum.SignupType
import com.road801.android.databinding.FragmentFindIdBinding
import com.road801.android.view.intro.login.LoginFragmentDirections

/**
 * MARK: - 아이디 찾기  [ 인증 ]
 *
 */
class FindIdFragment : Fragment() {
    private lateinit var binding: FragmentFindIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindIdBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }


    private fun setupListener() {
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(FindIdFragmentDirections.actionFindIdFragmentToFindIdCompleteFragment())
        }

    }

}