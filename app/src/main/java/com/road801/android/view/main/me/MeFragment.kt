package com.road801.android.view.main.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.road801.android.R
import com.road801.android.databinding.FragmentMeBinding
import com.road801.android.domain.model.SettingModel
import com.road801.android.domain.model.SettingType
import com.road801.android.view.main.me.adapter.MeRecyclerAdapter

class MeFragment : Fragment() {

    private lateinit var binding: FragmentMeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        return binding.root
    }

    private fun getSettingData(): List<SettingModel> = listOf(
        SettingModel(SettingType.PUSH, R.drawable.ic_setting_push, SettingType.PUSH.koString),
        SettingModel(SettingType.TERMS, R.drawable.ic_setting_terms, SettingType.TERMS.koString),
        SettingModel(SettingType.WITHDRAWAL ,R.drawable.ic_setting_withdrawal, SettingType.WITHDRAWAL.koString)
    )

    private fun initView() {
        val decoration = DividerItemDecoration(requireContext(), VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
        binding.recyclerView.adapter = MeRecyclerAdapter(getSettingData())
    }


}


