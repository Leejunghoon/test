package com.road801.android.view.main.home.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.road801.android.R
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.databinding.FragmentNewsBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.adapter.NewsRecyclerAdapter
import com.road801.android.view.main.home.event.EventFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
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
        initRecyclerView()
    }

    private fun setListener() {
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindViewModel() {
        viewModel.requestNewsInfo()

        viewModel.newsInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindNews(it.data.data)
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

    private fun initRecyclerView() {
        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._12dp).toInt())
        binding.recyclerView.addItemDecoration(spaceDecoration)
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!binding.recyclerView.canScrollVertically(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {

                }
            }
        })
    }


    private fun bindNews(items: List<NewsDto>) {
        val SCREEN_STORE_GUIDE = -1 // 매장안내
        val sortedItems = items.plusElement(makeFixNews()).sortedBy { it.id }
        binding.recyclerView.adapter = NewsRecyclerAdapter(sortedItems) {
            // item onClick
            if (it.id == SCREEN_STORE_GUIDE) findNavController().navigate(NewsFragmentDirections.actionNewsFragmentToStoreFragment())
            else  findNavController().navigate(NewsFragmentDirections.actionNewsFragmentToNewsDetailFragment(it.id))
        }
    }

    private fun makeFixNews(): NewsDto {
        return NewsDto(-1, title = "매장 안내",
            thumbnail = "https://d20d7iuoaqw83y.cloudfront.net/ICON/gas-pump.png",
            writeDt = "", )
    }




}