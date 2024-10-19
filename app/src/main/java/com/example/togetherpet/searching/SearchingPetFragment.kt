package com.example.togetherpet.searching

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togetherpet.DataStoreRepository
import com.example.togetherpet.R
import com.example.togetherpet.adapter.PetListAdapter
import com.example.togetherpet.adapter.SearchingBtnListAdapter
import com.example.togetherpet.databinding.FragmentSearchingPetBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.BadgeOptions
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class SearchingPetFragment : Fragment() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository
    private val searchingViewModel: SearchingViewModel by viewModels()

    private var _binding: FragmentSearchingPetBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchingBtnListAdapter: SearchingBtnListAdapter

    //Google Play 위치 API
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    private var kakaoMap: KakaoMap? = null

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchingPetBinding.inflate(inflater, container, false)

        //FusedLocationProviderClient 등록
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 권한 요청 초기화
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}

            override fun onMapError(p0: Exception?) {}

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                kakaoMap = p0
                checkLocationPermission()
            }

            override fun getZoomLevel(): Int {
                return 18
            }
        })

        return binding.root
    }

    private fun setMarker() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val labelManager: LabelManager? = kakaoMap?.labelManager

            searchingViewModel.missingPets.collectLatest { missingInfo ->
                for (pet in missingInfo) {
                    //url -> bitmap
                    val petImg = convertBitmapFromURL(pet.missingPetImgUrl)
                    //bitmap -> 원형
                    val circlePetImg = getBitmapCircleCrop(petImg, 120, 120)

                    val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.map_marker_icon)
                    val scaledMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, 220, 250, false)

                    val offsetY = 37 * resources.displayMetrics.density

                    val bitmapImg = circlePetImg?.let { combineBitmaps(scaledMarkerBitmap,it,offsetY) }

                    val markerStyle =
                        labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(bitmapImg)))

                    /*val imgStyle =
                        labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(circlePetImg)))*/

                    //위치 지정
                    val pos = LatLng.from(pet.missingLatitude, pet.missingLongitude)

                    //2) 라벨을 2개 띄우는 방법
                    /*val markerLabel =
                        labelManager?.layer?.addLabel(LabelOptions.from(pos).setStyles(markerStyle))
                    val imgLabel =
                        labelManager?.layer?.addLabel(LabelOptions.from(pos).setStyles(imgStyle))*/

                    //markerLabel?.addBadge(circlePetImg?.let { BadgeOptions.from(it).setOffset(0f,offsetY) })
                    //markerLabel?.addShareTransform(imgLabel)

                    //레이어 가져 오기
                    val layer = labelManager?.layer
                    //레이어 라벨 추가
                    layer?.addLabel(LabelOptions.from(pos).setStyles(markerStyle))
                    //layer?.addLabel(LabelOptions.from(pos).setStyles(imgStyle))
                }
            }
        }
    }

    // 두 비트맵을 하나로 합쳐 겹치는 이미지 생성
    private fun combineBitmaps(markerBitmap: Bitmap, petBitmap: Bitmap, offsetY: Float): Bitmap {
        // 마커 이미지 크기와 동일한 크기의 새 비트맵을 생성
        val resultBitmap = Bitmap.createBitmap(markerBitmap.width, markerBitmap.height, markerBitmap.config)
        val canvas = Canvas(resultBitmap)

        // 마커 비트맵을 먼저 그리기
        canvas.drawBitmap(markerBitmap, 0f, 0f, null)

        // 동물 비트맵을 Y축으로 offset을 주어 마커 이미지 위에 그리기
        val centerX = (markerBitmap.width - petBitmap.width) / 2f  // 중앙에 배치
        val petY = markerBitmap.height - petBitmap.height - offsetY  // 아래에서 offset만큼 위로 배치
        canvas.drawBitmap(petBitmap, centerX, petY, null)

        return resultBitmap
    }

    //추후에 로직 분리
    private fun getBitmapCircleCrop(bitmap: Bitmap?, width: Int, height: Int): Bitmap? {
        if (bitmap == null) {
            return null // bitmap이 null인 경우 null 반환
        }

        // 원을 그릴 때 가로와 세로 중 작은 쪽을 기준으로 크기를 설정
        val size = minOf(bitmap.width, bitmap.height)

        // 출력 비트맵을 정사각형으로 만듦
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            color = 0xff424242.toInt()
        }

        // 원을 그릴 때 중심점과 반지름을 설정 (정사각형 안에 원을 그림)
        val radius = size / 2f
        val centerX = size / 2f
        val centerY = size / 2f

        // 원형 영역 그리기
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 원형 안에 비트맵을 그리기 위한 Xfermode 설정
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // 원본 비트맵의 중앙 영역을 잘라서 원형 영역에 그리기
        val srcRect = Rect(
            (bitmap.width - size) / 2,  // 가로 중심점 계산
            (bitmap.height - size) / 2, // 세로 중심점 계산
            (bitmap.width + size) / 2,
            (bitmap.height + size) / 2
        )
        val destRect = Rect(0, 0, size, size)
        canvas.drawBitmap(bitmap, srcRect, destRect, paint)

        // 크기 조정
        return if (width != 0 && height != 0) {
            Bitmap.createScaledBitmap(output, width, height, false)
        } else {
            output
        }
    }


    //추후에 로직 분리
    private fun convertBitmapFromURL(url: String): Bitmap? {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream

        return BitmapFactory.decodeStream(input)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.researchingBtnList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.searchingMissingList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //btnList 사이의 간격 설정
        binding.researchingBtnList.addItemDecoration(ItemSpacing(20))

        //RecyclerView 초기화 시 실종 정보를 보여줌
        viewLifecycleOwner.lifecycleScope.launch {
            searchingViewModel.missingPets.collectLatest { missingInfo ->
                binding.searchingMissing.visibility = View.VISIBLE
                binding.myPetMissingRegisterButton.visibility = View.VISIBLE
                binding.searchingReportBtn.visibility = View.GONE
                binding.searchingMissingList.adapter =
                    PetListAdapter(requireContext(), missingInfo)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchingViewModel.loadData()
            dataStoreRepository.missingStatus.collect { isMissing ->
                searchingViewModel.petName.collect { petName ->
                    searchingBtnListAdapter =
                        SearchingBtnListAdapter(isMissing, petName) { clickedItem ->
                            when (clickedItem) {
                                "실종 정보" -> {
                                    binding.searchingMissing.visibility = View.VISIBLE
                                    binding.myPetMissingRegisterButton.visibility = View.VISIBLE
                                    binding.searchingReportBtn.visibility = View.GONE
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        searchingViewModel.missingPets.collectLatest { missingInfo ->
                                            binding.searchingMissingList.adapter =
                                                PetListAdapter(requireContext(), missingInfo)
                                        }
                                    }
                                }
                                //<추후> 제보 정보 데이터 적용
                                "제보 정보" -> {
                                    binding.myPetMissingRegisterButton.visibility = View.GONE
                                    binding.searchingMissing.visibility = View.GONE
                                    binding.searchingReportBtn.visibility = View.VISIBLE
                                }

                                petName -> {
                                    binding.myPetMissingRegisterButton.visibility = View.GONE
                                    binding.searchingMissing.visibility = View.GONE
                                    binding.searchingReportBtn.visibility = View.GONE
                                }
                            }
                        }
                    binding.researchingBtnList.adapter = searchingBtnListAdapter
                }
            }
        }
        /*//scroll 효과
        binding.searchingMissing.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                // RecyclerView 숨기기
                binding.searchingMissingList.visibility = View.GONE
            } else if (scrollY < oldScrollY) {
                // RecyclerView 다시 보이기
                binding.searchingMissingList.visibility = View.VISIBLE
            }
        }*/
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermission() {
        // 권한 허용 여부 확인
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            //권한 o -> 위치 정보 가져오기
            getCurrentLocation()
            setMarker()
        } else {
            //권한 x -> 권한 요청
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                latitude = it.latitude
                longitude = it.longitude
                Log.d("yeong", "현재 위치: $latitude / $longitude")
                displayLocation(latitude, longitude)
            }
    }

    private fun displayLocation(latitude: Double, longitude: Double) {
        val position = LatLng.from(latitude, longitude)
        kakaoMap?.moveCamera(
            CameraUpdateFactory.newCenterPosition(position)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }
}