package com.road801.android.view.intro.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.road801.android.BuildConfig
import com.road801.android.R
import com.road801.android.common.enum.LoginType
import com.road801.android.common.enum.SignupType
import com.road801.android.common.util.extension.TAG
import com.road801.android.common.util.extension.goToHome
import com.road801.android.common.util.extension.showDialog
import com.road801.android.common.util.validator.RoadValidator
import com.road801.android.databinding.FragmentLoginBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.intro.IntroViewModel

/**
 * MARK: - 로그인
 *
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: IntroViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }


    private fun bindViewModel() {
        // 로그인 성공 여부
        viewModel.isSuccessLogin.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (BuildConfig.DEBUG) Log.d(TAG, it.data.accessToken)
                        activity?.goToHome()
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


    private fun setupListener() {
        // 아이디 찾기
        binding.loginFindId.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFindIdFragment())
        }

        // 패스워드 찾기
        binding.loginFindPw.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFindPasswordFragment())
        }

        // 회원가입
        binding.loginSignupButton.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                    SignupType.ROAD801
                )
            )
        }

        // 로그인
        binding.loginButton.setOnClickListener {
            viewModel.requestLogin(
                LoginType.ROAD801,
                id = binding.loginIdEditText.text.toString().trim(),
                pw = binding.loginPasswordEditText.text.toString().trim()
            )
        }

        // 아이디
        binding.loginIdEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.loginIdTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // 패스워드
        binding.loginPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString().trim()
                validate(text, binding.loginPasswordTextInputLayout)
                checkValidAndNextButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun validate(text: String, inputLayout: TextInputLayout): Boolean {
        var emptyMessage = ""
        var validMessage = ""
        var isValid = false

        when (inputLayout) {
            binding.loginIdTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_id)
                validMessage = getString(R.string.input_valid_id)
                isValid = RoadValidator.id(text)
            }

            binding.loginPasswordTextInputLayout -> {
                emptyMessage = getString(R.string.input_empty_pw)
                validMessage = getString(R.string.input_valid_pw)
                isValid = RoadValidator.password(text)
            }
        }

        if (text.isEmpty()) {
            inputLayout.error = emptyMessage
            return false
        }

        if (!isValid) {
            inputLayout.error = validMessage
            return false
        }
        inputLayout.error = null
        inputLayout.isErrorEnabled = false
        inputLayout.helperText = " "
        return true
    }

    private fun checkValidAndNextButtonEnabled() {
        binding.loginButton.isEnabled =
            !binding.loginIdTextInputLayout.isErrorEnabled && !binding.loginPasswordTextInputLayout.isErrorEnabled
    }

}