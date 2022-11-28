package com.road801.android.view.main.me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.road801.android.R
import com.road801.android.common.enum.LoginType
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.goToIntro
import com.road801.android.common.util.extension.goToSystemSettingActivity
import com.road801.android.common.util.extension.showDialog
import com.road801.android.data.network.dto.MeDto
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.data.repository.SnsRepository
import com.road801.android.databinding.FragmentMeBinding
import com.road801.android.domain.model.SettingModel
import com.road801.android.domain.model.SettingType
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.me.adapter.MeRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeFragment : Fragment() {

    private lateinit var binding: FragmentMeBinding
    private val viewModel: MeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }

    private fun getSettingData(): List<SettingModel> = listOf(
        SettingModel(SettingType.PUSH, R.drawable.ic_setting_push, SettingType.PUSH.koString),
        SettingModel(SettingType.TERMS, R.drawable.ic_setting_terms, SettingType.TERMS.koString),
        SettingModel(SettingType.LOGOUT, R.drawable.ic_setting_withdrawal, SettingType.LOGOUT.koString),
        SettingModel(SettingType.WITHDRAWAL ,R.drawable.ic_setting_withdrawal, SettingType.WITHDRAWAL.koString)
    )

    private fun initView() {

    }



    private fun bindViewModel() {
        viewModel.requestMe()

        viewModel.meInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindMe(it.data)
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


    private fun bindMe(meDto: MeDto) {
        binding.recyclerView.adapter = MeRecyclerAdapter(this, meDto, getSettingData()) {
            when(it.type) {
                SettingType.PUSH -> findNavController().navigate(MeFragmentDirections.actionMeFragmentToAlarmFragment())
                SettingType.TERMS -> findNavController().navigate(MeFragmentDirections.actionMeFragmentToTermsFragment())
                SettingType.LOGOUT -> {

                    SnsRepository.logout(LoginType.valueOf(meDto.socialType.code)) {
                        showDialog(
                            parentFragmentManager,
                            title = "로드801",
                            "정말 로그아웃 하시겠습니까?",
                            cancelButtonTitle = "돌아가기",
                            confirmButtonTitle = "로그아웃",
                            listener = object : RoadDialog.OnDialogListener {
                                override fun onCancel() {
                                }

                                override fun onConfirm() {
                                    LocalDatabase.logOut()
                                    goToIntro()
                                }
                            }
                        )
                    }
                }
                SettingType.WITHDRAWAL -> findNavController().navigate(MeFragmentDirections.actionMeFragmentToWithdrawalFragment())
                else -> {}
            }
        }
    }






}


