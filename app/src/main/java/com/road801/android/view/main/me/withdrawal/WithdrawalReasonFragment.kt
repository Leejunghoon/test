package com.road801.android.view.main.me.withdrawal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.road801.android.databinding.FragmentWithdrawalReasonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawalReasonFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawalReasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawalReasonBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}