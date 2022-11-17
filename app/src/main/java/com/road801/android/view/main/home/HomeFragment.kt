package com.road801.android.view.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
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
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var newsPagerAdapter: HomeEventPagerAdapter
    private lateinit var eventPagerAdapter: HomeEventPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        initPager()
        setListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        showNewsBanner()
    }

    private fun initView() {
        binding.homeBarcodeTextView.visibility = View.GONE
        binding.homeSegmentRadioGroup.check(binding.homeSegmentQr.id)
    }

    private fun initPager() {
        newsPagerAdapter = HomeEventPagerAdapter()
        eventPagerAdapter = HomeEventPagerAdapter()

        binding.homeNewsViewPager.adapter = newsPagerAdapter
        binding.homeEventViewPager.adapter = eventPagerAdapter

        binding.homeNewsViewPager.offscreenPageLimit = 3
        binding.homeNewsViewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.homeEventViewPager.offscreenPageLimit = 3
        binding.homeEventViewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.homeNewsIndicator.isAttachedToWindow.not().apply {
            binding.homeNewsIndicator.attachTo(binding.homeNewsViewPager)
            binding.homeEventIndicator.attachTo(binding.homeEventViewPager)
        }
    }

    private fun setupPager(items: List<EventDto>) {
        newsPagerAdapter.setItems(items)
        eventPagerAdapter.setItems(items)
    }

    private fun bindViewModel() {
        viewModel.requestHomeInfo()
        viewModel.requestHomeEventInfo()

        viewModel.homeInfo.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Log.d("hoon", """bindHomeInfo""")
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
                        Log.d("hoon", """bindHomeEventInfo""")
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

        // 소식
        binding.homeNewsContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewsFragment())
        }

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

        user.barcode.getBarcodeBitmap(
            BarcodeFormat.QR_CODE,
            resources.getDimension(R.dimen._200dp).toInt(),
            resources.getDimension(R.dimen._200dp).toInt()
        ).apply {
            this?.let {
                Glide.with(requireContext())
                    .load(it)
                    .override(resources.getDimension(R.dimen._200dp).toInt())
                    .into(binding.homeQrImageView)
            }
        }

         user.barcode.getBarcodeBitmap(
            BarcodeFormat.CODE_128,
            resources.getDimension(R.dimen._300dp).toInt(),
            resources.getDimension(R.dimen._80dp).toInt()
        ).apply {
             this?.let {
                 Glide.with(requireContext())
                     .load(it)
                     .override( resources.getDimension(R.dimen._300dp).toInt(),
                         resources.getDimension(R.dimen._80dp).toInt())
                     .into(binding.homeBarcodeImageView)
             }
         }
//        binding.homeQrImageView.setImageBitmap(qr)
//        binding.homeBarcodeImageView.setImageBitmap(barcode)



        binding.homeBarcodeTextView.text = user.barcode

        binding.homeTitle.text = buildString {
            append("안녕하세요. ")
            append(user.name)
            append(" 회원님!")
        }

        with(user.profileImage){
            if (!this.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(this)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.homeProfileImageView)
            }
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