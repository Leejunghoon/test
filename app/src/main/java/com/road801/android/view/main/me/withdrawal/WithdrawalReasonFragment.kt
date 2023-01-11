package com.road801.android.view.main.me.withdrawal

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import com.road801.android.common.enum.LoginType
import com.road801.android.common.util.extension.goToIntro
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.extension.showToast
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.data.repository.SnsRepository
import com.road801.android.databinding.FragmentWithdrawalReasonBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.me.MeViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * MARK: - 탈퇴 사유
 *
 */
@AndroidEntryPoint
class WithdrawalReasonFragment : Fragment() {

    private lateinit var binding: FragmentWithdrawalReasonBinding
    private val viewModel: MeViewModel by viewModels()
    private val checkBoxes: MutableList<MaterialCheckBox> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawalReasonBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        checkBoxes.add(binding.reason1CheckBox)
        checkBoxes.add(binding.reason2CheckBox)
        checkBoxes.add(binding.reason3CheckBox)
        checkBoxes.add(binding.reason4CheckBox)
    }


    private fun setupListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextButton.setOnClickListener {
            if (checkBoxes.none { it.isChecked }) {
                showToast("탈퇴 사유를 선택해주세요.")
                return@setOnClickListener
            }

            showDialog(
                parentFragmentManager,
                title = "서비스 해지 및 탈퇴",
                "보유하신 포인트와 쿠폰등\n모든 정보가 소멸됩니다.\n\n정말 탈퇴하시겠습니까?",
                cancelButtonTitle = "탈퇴하기",
                confirmButtonTitle = "돌아가기",
                listener = object : RoadDialog.OnDialogListener {
                    override fun onCancel() {
                        viewModel.requestWithdrawal(checkBoxes.filter { it.isChecked }.joinToString("\n") { it.text })
                    }

                    override fun onConfirm() {
                        findNavController().navigate(WithdrawalReasonFragmentDirections.actionWithdrawalReasonFragmentToMeFragment())
                    }
                }
            )
        }

    }

    private fun bindViewModel() {
        viewModel.isDrop.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        viewModel.getMe()?.let { me ->
                            me.socialType?.let { type ->
                                SnsRepository.withdrawal(requireContext(), LoginType.valueOf(type.code)) {
                                    // completion
                                }
                            }
                        }
                        LocalDatabase.logOut()
                        showToast("회원 탈퇴가 완료되었습니다.")
                        goToIntro()
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