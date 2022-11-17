package com.road801.android.view.main.home

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.road801.android.R
import com.road801.android.common.util.extension.*
import com.road801.android.common.util.transformer.ZoomOutPageTransformer
import com.road801.android.data.network.dto.EventDto
import com.road801.android.data.network.dto.response.HomeEventResponseDto
import com.road801.android.data.network.dto.response.HomeResponseDto
import com.road801.android.databinding.FragmentHomeBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.main.home.adapter.HomeEventPagerAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("hoon", """onCreateView""")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d("hoon", """onCreateView""")
        initView()
        setListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("hoon", """onViewCreated""")
        bindViewModel()
        showNewsBanner()

    }

    private fun initView() {
        binding.homeBarcodeTextView.visibility = View.GONE
        binding.homeSegmentRadioGroup.check(binding.homeSegmentQr.id)
    }

    private fun setupPager(items: List<EventDto>) {
        val newsPagerAdapter = HomeEventPagerAdapter(items)
        val eventPagerAdapter = HomeEventPagerAdapter(items)
        binding.homeNewsViewPager.offscreenPageLimit = 3
        binding.homeNewsViewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.homeEventViewPager.offscreenPageLimit = 3
        binding.homeEventViewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.homeNewsViewPager.adapter = newsPagerAdapter
        binding.homeEventViewPager.adapter = eventPagerAdapter

        binding.homeNewsIndicator.attachTo(binding.homeNewsViewPager)
        binding.homeEventIndicator.attachTo(binding.homeEventViewPager)
    }

    private fun bindViewModel() {
        viewModel.requestHomeInfo()
        viewModel.requestHomeEventInfo()

        viewModel.homeInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindHomeInfo(it.data)
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

        viewModel.homeEventInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        bindHomeEventInfo(it.data)
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

    private fun setListener() {

        // 소식 더보기
        binding.homeRoadNewsMoreButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewsFragment())
        }

        // 이벤트 더보기
        binding.homeRoadEventMoreButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEventFragment())
        }

        // QR, 바코드 선택
        binding.homeSegmentRadioGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                binding.homeSegmentQr.id -> {
                    if (isChecked) {
                        binding.homeQrImageView.visibility = View.VISIBLE
                        binding.homeBarcodeImageView.visibility = View.GONE
                        binding.homeBarcodeTextView.visibility = View.GONE
                    }
                }

                binding.homeSegmentBarcode.id -> {
                    if (isChecked) {
                        binding.homeQrImageView.visibility = View.GONE
                        binding.homeBarcodeImageView.visibility = View.VISIBLE
                        binding.homeBarcodeTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun bindHomeInfo(item: HomeResponseDto) {
        val user = item.customerInfo
        val asset = item.pointInfo

        val qr = user.barcode.getBarcodeBitmap(
            BarcodeFormat.QR_CODE,
            resources.getDimension(R.dimen._300dp).toInt(),
            resources.getDimension(R.dimen._300dp).toInt()
        )

        val barcode = user.barcode.getBarcodeBitmap(
            BarcodeFormat.CODE_128,
            resources.getDimension(R.dimen._300dp).toInt(),
            resources.getDimension(R.dimen._80dp).toInt()
        )

        Glide.with(requireContext())
            .load(qr)
            .apply(RequestOptions.overrideOf(resources.getDimension(R.dimen._200dp).toInt()))
            .into(binding.homeQrImageView)

        Glide.with(requireContext())
            .load(barcode)
            .apply(
                RequestOptions.overrideOf(
                    resources.getDimension(R.dimen._300dp).toInt(),
                    resources.getDimension(R.dimen._80dp).toInt()
                )
            )
            .into(binding.homeBarcodeImageView)



        binding.homeBarcodeTextView.text = user.barcode

        binding.homeTitle.text = buildString {
            append("안녕하세요. ")
            append(user.name)
            append(" 회원님!")
        }
        binding.homeGradeTextView.text = buildString {
            append(user.rating.code)
            append(" 회원님")
        }

        binding.homeExpireDateTextView.text =
            if (asset.nextExpireDate.isNullOrEmpty()) {
                "포인트를 적립해보세요!"
            } else {
                buildString {
                    append(asset.nextExpirePoint)
                    append(" 포인트 만료기간: ")
                    append(asset.nextExpireDate.formatted("yyyy년 MM월 dd일"))
                }
            }

        binding.homePointTextView.text = buildString {
            append(asset.point.currency)
            append(" P")
        }
    }

    private fun bindHomeEventInfo(item: HomeEventResponseDto) {
        setupPager(item.eventList)
    }

    private fun showNewsBanner() {
        binding.homeNewsBanner.run {
            visibility = View.VISIBLE
            binding.homeNewsRedDot.visibility = View.VISIBLE
            startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.floating))
        }
    }

}