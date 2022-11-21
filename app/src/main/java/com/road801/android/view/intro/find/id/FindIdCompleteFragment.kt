package com.road801.android.view.intro.find.id

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.road801.android.databinding.FragmentFindIdCompleteBinding

/**
 * MARK: - 아이디 찾기  [ 완료 ]
 *
 */
class FindIdCompleteFragment : Fragment() {

    private lateinit var binding: FragmentFindIdCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindIdCompleteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    private fun setupListener() {
        binding.nextButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}