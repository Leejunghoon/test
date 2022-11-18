package com.road801.android.view.main.me.withdrawal

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.road801.android.databinding.FragmentWithdrawalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawalFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        return binding.root
    }


    private fun initView() {
        binding.withdrawalMessageTextView.movementMethod = ScrollingMovementMethod()
    }

}