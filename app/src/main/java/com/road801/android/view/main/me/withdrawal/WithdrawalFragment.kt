package com.road801.android.view.main.me.withdrawal

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.road801.android.databinding.FragmentWithdrawalBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 서비스 해지 및 탈퇴
 *
 */
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
        setupListener()
        return binding.root
    }

    private fun initView() {
        binding.withdrawalMessageTextView.movementMethod = ScrollingMovementMethod()
    }


    private fun setupListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(WithdrawalFragmentDirections.actionWithdrawalFragmentToWithdrawalReasonFragment())
        }

        binding.withdrawalAgreeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.nextButton.isEnabled = isChecked
        }
    }
}
