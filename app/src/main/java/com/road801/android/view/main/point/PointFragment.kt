package com.road801.android.view.main.point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.road801.android.R
import com.road801.android.common.util.extension.currency
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.EventDto
import com.road801.android.data.network.dto.PointHistoryDto
import com.road801.android.data.network.dto.response.CommonListResponseDto
import com.road801.android.databinding.FragmentPointBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.HomeViewModel
import com.road801.android.view.main.home.adapter.EventRecyclerAdapter
import com.road801.android.view.main.home.event.EventDetailFragmentArgs
import com.road801.android.view.main.home.event.EventFragmentDirections
import com.road801.android.view.main.home.event.EventViewModel
import com.road801.android.view.main.point.adapter.PointRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointFragment : Fragment() {

    private lateinit var binding: FragmentPointBinding
    private val viewModel: PointViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPointBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        bindViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initView() {
        binding.pointGradeTextView.text = "${homeViewModel.userGrade.value} 등급"
        binding.pointPointTextView.text = "${homeViewModel.userPoint.value?.currency} P"
    }

    private fun setupRecyclerView(items: List<PointHistoryDto>) {
        binding.pointEmptyContainer.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE

        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._12dp).toInt())
        binding.recyclerView.addItemDecoration(spaceDecoration)
        binding.recyclerView.adapter = PointRecyclerAdapter(items)
    }

    private fun bindViewModel() {
        viewModel.requestPointHistory()

        viewModel.pointHistoryInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        setupRecyclerView(it.data.data)
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


