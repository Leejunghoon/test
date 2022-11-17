package com.road801.android.view.main.home.event


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView(items: List<EventDto>) {
        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._12dp).toInt())
        binding.recyclerView.addItemDecoration(spaceDecoration)

        binding.recyclerView.adapter = EventRecyclerAdapter(items) {
            // item onClick
            findNavController().navigate(EventFragmentDirections.actionEventFragmentToEventDetailFragment(it.id))
        }
    }

    private fun bindViewModel() {
        viewModel.requestEvent()

        viewModel.eventInfo.observe(viewLifecycleOwner) { result ->
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