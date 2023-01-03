package com.road801.android.view.main.home.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.road801.android.R
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.transformer.VerticalSpaceItemDecoration
import com.road801.android.data.network.dto.AlertDto
import com.road801.android.databinding.FragmentAlertBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.adapter.AlertRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding
    private val viewModel: AlertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
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

    private fun bindViewModel() {
        viewModel.requestAlertInfo()

        viewModel.alertInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindAlert(it.data.data)
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
        val spaceDecoration = VerticalSpaceItemDecoration(resources.getDimension(R.dimen._20dp).toInt())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), VERTICAL)
        binding.recyclerView.addItemDecoration(spaceDecoration)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!binding.recyclerView.canScrollVertically(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.requestMoreAlertInfo()
                }
            }
        })
    }


    private fun bindAlert(items: List<AlertDto>) {
        showNoneView(if (items.isEmpty()) VISIBLE else GONE)
        val beforeItemCount = binding.recyclerView.adapter?.itemCount

        binding.recyclerView.adapter = AlertRecyclerAdapter(items.sortedByDescending { it.createDt })

        beforeItemCount?.let {
            if (items.size > 20) binding.recyclerView.smoothScrollToPosition(beforeItemCount+1)
        }
    }

    private fun showNoneView(visibility: Int) {
        binding.noneView.visibility = visibility
    }
}