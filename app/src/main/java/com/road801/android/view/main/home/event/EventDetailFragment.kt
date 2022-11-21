package com.road801.android.view.main.home.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.common.util.extension.currency
import com.road801.android.common.util.extension.formatted
import com.road801.android.common.util.extension.showDialog
import com.road801.android.data.network.dto.EventDto
import com.road801.android.databinding.FragmentEventDetailBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.me.withdrawal.WithdrawalReasonFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment: Fragment() {
    private lateinit var binding: FragmentEventDetailBinding
    private val viewModel: EventViewModel by viewModels()
    private val args: EventDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
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

    }

    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextButton.setOnClickListener {
            viewModel.requestEventEnter(args.eventId)
        }
    }

    private fun bindViewModel() {
        viewModel.requestEventDetail(args.eventId)

        viewModel.eventDetail.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindEventDetail(it.data)
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

        viewModel.eventCurrentPoint.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        showDialog(parentFragmentManager, title = "이벤트 참여 완료!",
                            message = "${it.data.point.currency}P 획득",
                            listener = object : RoadDialog.OnDialogListener {
                            override fun onCancel() {
                            }

                            override fun onConfirm() {
                                findNavController().popBackStack()
                            }
                        })
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

    private fun bindEventDetail(item: EventDto) {
        binding.toolbar.title = item.title
        binding.eventDetailTitleTextView.text = item.title
        binding.eventDetailContentTextView.text = item.content
        binding.eventDetailDateTextView.text = buildString {
            append(item.startDt?.formatted("기간 "))
            append(item.startDt?.formatted("yyyy.MM.dd"))
            append(" ~ ")
            append(item.endDt?.formatted("yyyy.MM.dd"))
        }

        item.image?.let {
            Glide.with(requireContext())
                .load(item.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.eventDetailImageView)
        }

        binding.nextButton.visibility = if(item.isPromotion) View.VISIBLE else View.GONE
    }
}