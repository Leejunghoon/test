package com.road801.android.view.main.home.event


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.road801.android.R
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.EventDto
import com.road801.android.databinding.FragmentEventBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.adapter.EventRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EventFragment: Fragment() {

    private lateinit var binding: FragmentEventBinding
    private val viewModel: EventViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        setListener()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()
    }

    private fun initView() {
        initRecyclerView()
    }

    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initRecyclerView() {
        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._20dp).toInt())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(spaceDecoration)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!binding.recyclerView.canScrollVertically(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.requestMoreEvent()
                }
            }
        })
    }

    private fun bindEvent(items: List<EventDto>) {
        val beforeItemCount = binding.recyclerView.adapter?.itemCount
        binding.recyclerView.adapter = EventRecyclerAdapter(items.sortedByDescending { it.id }) {
            // ????????? ????????? ??????
            findNavController().navigate(EventFragmentDirections.actionEventFragmentToEventDetailFragment(it.id))
        }
        beforeItemCount?.let {
            if (items.size > 20) binding.recyclerView.smoothScrollToPosition(beforeItemCount+1)
        }
    }

    private fun bindViewModel() {
        viewModel.requestEvent()

        viewModel.eventInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindEvent(it.data.data)
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