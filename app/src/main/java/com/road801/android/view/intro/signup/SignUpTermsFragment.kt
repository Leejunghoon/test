package com.road801.android.view.intro.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.road801.android.common.enum.TermType
import com.road801.android.databinding.FragmentSignUpTermsBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * MARK: - 회원가입  [ 이용 약관 동의 ]
 *
 */

@AndroidEntryPoint
class SignUpTermsFragment : Fragment() {

    private lateinit var binding: FragmentSignUpTermsBinding
    private val args: SignUpTermsFragmentArgs by navArgs()
    private val termsList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpTermsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }


    private fun setupListener() {
        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToSignUpInfoInputFragment(args.signupType, termsList.toTypedArray()))
        }

        // USE, PRIVACY, LOCATION, MARKETING
        //  로드801 서비스 이용 동의
        binding.signupUseCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) termsList.add(TermType.USE.name)
            else termsList.remove(TermType.USE.name)
            binding.nextButton.isEnabled = isSelectedRequiredItem()
        }

        // 개인정보 제공 동의
        binding.signupPrivacyCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) termsList.add(TermType.PRIVACY.name)
            else termsList.remove(TermType.PRIVACY.name)
            binding.nextButton.isEnabled = isSelectedRequiredItem()
        }

        // 위치정보 제공 동의
        binding.signupGpsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) termsList.add(TermType.LOCATION.name)
            else termsList.remove(TermType.LOCATION.name)
        }

        // 마케팅 약관 동의
        binding.signupMarketingCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) termsList.add(TermType.MARKETING.name)
            else termsList.remove(TermType.MARKETING.name)
        }


        binding.signupUseLinkButton.setOnClickListener {
            findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToBaseWebView("로드801 서비스 이용 약관", "https://spurious-lime-3d4.notion.site/801-8832f2f2a7bc46ec8a1b64b837baab7e"))
        }

        binding.signupPrivacyLinkButton.setOnClickListener {
            findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToBaseWebView("개인정보 제공 동의 약관", "https://spurious-lime-3d4.notion.site/24b3a8fc62ae49e291f48cd55818a4d3"))
        }

        binding.signupGpsLinkButton.setOnClickListener {
            findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToBaseWebView("위치정보 제공 동의 약관", ""))
        }

        binding.signupMarketingLinkButton.setOnClickListener {
            findNavController().navigate(SignUpTermsFragmentDirections.actionSignUpTermsFragmentToBaseWebView("마케팅 동의 약관", "https://spurious-lime-3d4.notion.site/df53f4f312c3403292a1547b5ad9d6c2"))
        }


    }

    private fun isSelectedRequiredItem(): Boolean {
        return binding.signupUseCheckBox.isChecked && binding.signupPrivacyCheckBox.isChecked
    }


}