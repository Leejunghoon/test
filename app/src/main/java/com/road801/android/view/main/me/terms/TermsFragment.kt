package com.road801.android.view.main.me.terms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.road801.android.R
import com.road801.android.databinding.FragmentFindPasswordCompleteBinding
import com.road801.android.databinding.FragmentTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsFragment : Fragment() {
    private lateinit var binding: FragmentTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTermsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()
        return binding.root
    }


    private fun setListener() {
        binding.termsUsePolicyContainer.setOnClickListener {
            findNavController().navigate(TermsFragmentDirections.actionTermsFragmentToHomeBaseWebView("로드801 서비스 이용 약관", "https://spurious-lime-3d4.notion.site/801-8832f2f2a7bc46ec8a1b64b837baab7e"))
        }

        binding.termsPrivacyContainer.setOnClickListener {
            findNavController().navigate(TermsFragmentDirections.actionTermsFragmentToHomeBaseWebView("개인정보 제공 동의 약관", "https://spurious-lime-3d4.notion.site/24b3a8fc62ae49e291f48cd55818a4d3"))
        }

        binding.termsGpsPolicyContainer.setOnClickListener {
            findNavController().navigate(TermsFragmentDirections.actionTermsFragmentToHomeBaseWebView("위치정보 제공 동의 약관", ""))
        }

        binding.termsMarketingContainer.setOnClickListener {
            findNavController().navigate(TermsFragmentDirections.actionTermsFragmentToHomeBaseWebView("마케팅 동의 약관", "https://spurious-lime-3d4.notion.site/df53f4f312c3403292a1547b5ad9d6c2"))
        }
    }

}