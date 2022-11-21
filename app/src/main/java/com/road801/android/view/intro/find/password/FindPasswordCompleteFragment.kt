package com.road801.android.view.intro.find.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.road801.android.common.util.extension.showDialog
import com.road801.android.databinding.FragmentFindPasswordCompleteBinding
import com.road801.android.view.dialog.RoadDialog.OnDialogListener

/**
 * MARK: - 비밀번호 찾기  [ 완료 ]
 *
 */
class FindPasswordCompleteFragment : Fragment() {
    private lateinit var binding: FragmentFindPasswordCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindPasswordCompleteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    private fun setupListener() {

        binding.nextButton.setOnClickListener {
            showDialog(
                parentFragmentManager,
                title = "비밀번호 재설정 완료",
                "로그인 화면으로 이동합니다.",
                listener = object : OnDialogListener {
                    override fun onCancel() {
                    }

                    override fun onConfirm() {
                        findNavController().popBackStack()
                    }
                })
        }

    }
}