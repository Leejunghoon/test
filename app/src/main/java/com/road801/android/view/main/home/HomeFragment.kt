package com.road801.android.view.main.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.zxing.BarcodeFormat
import com.road801.android.R
import com.road801.android.common.fcm.RoadFirebaseMessagingService
import com.road801.android.common.util.extension.*
import com.road801.android.common.util.permission.PermissionManager
import com.road801.android.common.util.transformer.ItemHorizontalDecoration
import com.road801.android.data.network.dto.EventDto
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.data.network.dto.response.HomeEventResponseDto
import com.road801.android.data.network.dto.response.HomeResponseDto
import com.road801.android.databinding.FragmentHomeBinding
import com.road801.android.domain.transfer.Resource
import com.road801.android.view.dialog.EventDialog
import com.road801.android.view.dialog.RoadDialog
import com.road801.android.view.main.home.adapter.HomeEventPagerAdapter
import com.road801.android.view.main.home.adapter.HomeNewsPagerAdapter
import com.road801.android.view.main.me.MeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val meViewModel: MeViewModel by viewModels()

    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    private lateinit var newsPagerAdapter: HomeNewsPagerAdapter
    private lateinit var eventPagerAdapter: HomeEventPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishDoubleBack()

        RoadFirebaseMessagingService.geToken {
            meViewModel.updateDeviceID(it)
        }
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
    }

    private fun setFinishDoubleBack() {
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val tempTime = System.currentTimeMillis();
                    val intervalTime = tempTime - backPressedTime;
                    if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                        super.setEnabled(false)
                    } else {
                        backPressedTime = tempTime;
                        showToast("'뒤로' 버튼을 한 번 더 누르면 종료됩니다.")
                        return
                    }
                }
            })
    }
    private fun initView() {
        binding.homeSegmentRadioGroup.check(binding.homeSegmentBarcode.id)
    }

    private fun initPager() {
        newsPagerAdapter = HomeNewsPagerAdapter() {
            // 소식 상세로 이동
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewsDetailFragment(it.id))
        }
        eventPagerAdapter = HomeEventPagerAdapter() {
            // 이벤트 상세로 이동
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEventDetailFragment(it.id))
        }

        val newsManager = object : LinearLayoutManager(requireContext()) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                val width = (width * 0.6).roundToInt()
                val height = (width + requireContext().resources.getDimension(R.dimen._20dp)).roundToInt()
                lp.width = width
                lp.height = height
                return true
            }
        }

        val eventManager = object : LinearLayoutManager(requireContext()) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                val width = (width * 0.6).roundToInt()
                val height = ((width * 1.4) + requireContext().resources.getDimension(R.dimen._20dp)).roundToInt()
                lp.width = width
                lp.height = height
                return true
            }
        }

        newsManager.orientation = HORIZONTAL
        eventManager.orientation = HORIZONTAL

        binding.homeNewsViewPager.layoutManager = newsManager
        binding.homeEventViewPager.layoutManager = eventManager

        binding.homeNewsViewPager.addItemDecoration(ItemHorizontalDecoration(requireContext().resources.getDimension(R.dimen.page_offset).toInt()))
        binding.homeEventViewPager.addItemDecoration(ItemHorizontalDecoration(requireContext().resources.getDimension(R.dimen.page_offset).toInt()))

        binding.homeNewsViewPager.adapter = newsPagerAdapter
        binding.homeEventViewPager.adapter = eventPagerAdapter

    }

    private fun setupPager(news: List<NewsDto>, events: List<EventDto>) {
        newsPagerAdapter.setItems(news)
        eventPagerAdapter.setItems(events)
    }

    private fun bindViewModel() {
        viewModel.requestHomeInfo()
        viewModel.requestHomeEventInfo()
        viewModel.findNews()

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
                        showEventPopup(it.data.popup)
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

        viewModel.isNew.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        showNewsBanner(it.data)
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

        binding.homeRoadCallImageView.setOnClickListener {
            val permissionManager = PermissionManager.from(this@HomeFragment)
            if (permissionManager.hasPermission(this, Manifest.permission.CALL_PHONE).not()) {
                showDialog(
                    parentFragmentManager,
                    title = "권한 알림",
                    "로드801 설정에서 전화 권한을 수락해주세요.",
                    cancelButtonTitle = "거부하기",
                    confirmButtonTitle = "설정하기",
                    listener = object : RoadDialog.OnDialogListener {
                        override fun onCancel() {
                        }

                        override fun onConfirm() {
                            goToSystemSettingActivity()
                        }
                    }
                )
            } else {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:0328132000")))
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
                    .transition(DrawableTransitionOptions.withCrossFade())
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
                     .transition(DrawableTransitionOptions.withCrossFade())
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

        if (user.profileImage.isNullOrEmpty().not()) {
            Glide.with(requireContext())
                .load(user.profileImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.homeProfileImageView)
        }

        binding.homeGradeTextView.text = buildString {
            append(user.rating.value)
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
        setupPager(item.boardList, item.eventList)
    }

    private fun showNewsBanner(isShow: Boolean) {
        binding.homeNewsBanner.run {
            if (isShow) {
                visibility = View.VISIBLE
                binding.homeNewsRedDot.visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.floating))
            } else {
                visibility = View.INVISIBLE
                binding.homeNewsRedDot.visibility = View.INVISIBLE
                clearAnimation()
            }
        }
    }

    private fun showEventPopup(item: EventDto?) {
        item?.let {
            val dialog = EventDialog()
            dialog.title = it.title
            dialog.onClickListener = object : EventDialog.OnDialogListener {
                override fun onConfirm() {
                    // 이벤트 상세로 이동
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEventDetailFragment(item.id))
                }
            }
            dialog.show(parentFragmentManager, "showEventDialog")

        }
    }

}