package com.road801.android.view.intro.find.password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.road801.android.R
import com.road801.android.databinding.FragmentFindPasswordBinding
import com.road801.android.databinding.FragmentHomeBinding
import com.road801.android.view.intro.find.id.FindIdFragmentDirections

/**
 * MARK: - 비밀번호 찾기  [ 인증 ]
 *
 */
class FindPasswordFragment : Fragment() {

    private lateinit var binding: FragmentFindPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    private fun setupListener() {
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(FindPasswordFragmentDirections.actionFindPasswordFragmentToFindPasswordCompleteFragment())
        }

    }
}