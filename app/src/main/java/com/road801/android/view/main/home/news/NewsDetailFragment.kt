package com.road801.android.view.main.home.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.road801.android.common.util.extension.showDialog
import com.road801.android.data.network.dto.NewsDetailDto
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.databinding.FragmentNewsDetailBinding
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsDetailFragment: Fragment() {
    private lateinit var binding: FragmentNewsDetailBinding
    private val viewModel: NewsViewModel by viewModels()
    private val args: NewsDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setListener()
        bindViewModel()

    }

    private fun initView() {

    }

    private fun setListener() {
        binding.newDetailBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindViewModel() {
        viewModel.requestNewsDetail(args.boardId)

        viewModel.newsDetail.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindNewsDetail(it.data)
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

    private fun bindNewsDetail(item: NewsDetailDto) {
        binding.newDetailTitleTextView.text = item.subtitle
        binding.newDetailContentTextView.text = item.content

        Glide.with(requireContext())
            .load(item.image)
            .into(binding.newDetailImageView)
    }
}