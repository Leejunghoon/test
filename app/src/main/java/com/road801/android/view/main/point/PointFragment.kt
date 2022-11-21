package com.road801.android.view.main.point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.road801.android.R
import com.road801.android.common.util.extension.currency
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.PointHistoryDto
import com.road801.android.data.network.dto.response.HomeResponseDto
import com.road801.android.databinding.FragmentPointBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.HomeViewModel
import com.road801.android.view.main.point.adapter.PointRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointFragment : Fragment() {

    private lateinit var binding: FragmentPointBinding
    private val viewModel: PointViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPointBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setUserInfo(grade: String, point: Int) {
        binding.pointGradeTextView.text = "$grade 등급"
        binding.pointPointTextView.text = "${point.currency} P"
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

        homeViewModel.homeInfo.observe(viewLifecycleOwner) { result ->
            when (result.peekContent()) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val it = (result.peekContent() as Resource.Success<HomeResponseDto>).data
                    val grade = it.customerInfo.rating.value
                    val point = it.pointInfo.point
                    setUserInfo(grade, point)
                }
                is Resource.Failure -> {
                }
            }
        }
    }

}


