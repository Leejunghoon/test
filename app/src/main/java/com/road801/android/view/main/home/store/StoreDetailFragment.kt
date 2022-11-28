package com.road801.android.view.main.home.store

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.road801.android.common.util.extension.goToSystemSettingActivity
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.permission.Permission
import com.road801.android.common.util.permission.PermissionManager
import com.road801.android.data.network.dto.StoreDetailDto
import com.road801.android.databinding.FragmentStoreDetailBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.me.withdrawal.WithdrawalReasonFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoreDetailFragment : Fragment() {

    private lateinit var binding: FragmentStoreDetailBinding
    private val viewModel: StoreViewModel by viewModels()
    private val args: StoreDetailFragmentArgs by navArgs()
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        setListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }

    private fun initView() {
        // init permission
        permissionManager = PermissionManager.from(this@StoreDetailFragment)
    }

    private fun setListener() {
        binding.storeDetailBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.storeDetailReserveButton.setOnClickListener {
            if (permissionManager.hasPermission(this, Manifest.permission.CALL_PHONE).not()) {
                showDialog(
                    parentFragmentManager,
                    title = "권한 알림",
                    "로드801 설정에서 전화 권한을 수락해주세요.",
                    cancelButtonTitle = "거부하기",
                    confirmButtonTitle = "설정하기",
                    listener = object : RoadDialog.OnDialogListener {
                        override fun onCancel() {
                        }

                        override fun onConfirm() {
                            goToSystemSettingActivity()
                        }
                    }
                )
            } else {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${viewModel.getTel()}")))
            }
        }
    }

    private fun bindViewModel() {
        viewModel.requestStoreDetail(args.storeId)

        viewModel.storeDetail.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindStoreDetail(it.data)
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

    private fun bindStoreDetail(item: StoreDetailDto) {
        binding.storeDetailTitleTextView.text = item.name
        binding.storeDetailContentTextView.text =
            buildString {
                append(item.introduce)
                append("\n\n")
                append(item.address)
                append("\n연락처: ")
                append(item.phone)
            }


//        if (item.image.isNullOrEmpty().not()) {
//            Glide.with(requireContext())
//                .load(item.image)
//                .centerCrop()
//                .into(binding.newDetailImageView)
//        }
    }
}