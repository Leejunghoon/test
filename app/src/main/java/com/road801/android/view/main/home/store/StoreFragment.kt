package com.road801.android.view.main.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.road801.android.R
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.data.network.dto.StoreDto
import com.road801.android.databinding.FragmentNewsBinding
import com.road801.android.databinding.FragmentStoreBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.adapter.NewsRecyclerAdapter
import com.road801.android.view.main.home.adapter.StoreRecyclerAdapter
import com.road801.android.view.main.home.news.NewsFragmentDirections
import com.road801.android.view.main.home.news.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoreFragment : Fragment() {

    private lateinit var binding: FragmentStoreBinding
    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
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
    }

    private fun bindViewModel() {
        viewModel.requestStoreInfo()

        viewModel.storeInfo.observe(viewLifecycleOwner) { result ->
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

    private fun setupRecyclerView(items: List<StoreDto>) {
        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._12dp).toInt())
        binding.recyclerView.addItemDecoration(spaceDecoration)
        binding.recyclerView.adapter = StoreRecyclerAdapter(items.sortedBy { it.id }) {
            // item onClick
            findNavController().navigate(StoreFragmentDirections.actionStoreFragmentToStoreDetailFragment(it.id))
        }
    }
}

