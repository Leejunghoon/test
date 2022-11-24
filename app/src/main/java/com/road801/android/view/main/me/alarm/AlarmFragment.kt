package com.road801.android.view.main.me.alarm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.road801.android.common.util.extension.goToIntro
import com.road801.android.common.util.extension.showDialog
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.databinding.FragmentAlarmBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.me.MeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private val viewModel: MeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }


    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.alarmMarketingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.requestPushAd(isChecked)
        }
    }

    private fun bindViewModel() {
        viewModel.isActivePushMarketing.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        var message = ""
                        if (it.data)  message = "로드801 마케팅 정보를 받습니다."
                        else message = "로드801 마케팅 정보를 받지 않습니다."
                        Snackbar.make(binding.root, message, 1000).show()
                    }
                    is Resource.Failure -> {
                        showDialog(
                            parentFragmentManager,
                            title = it.exception.domainErrorMessage,
                            message = it.exception.domainErrorSubMessage
                        )
                    }
                }
            }
        }
    }

}