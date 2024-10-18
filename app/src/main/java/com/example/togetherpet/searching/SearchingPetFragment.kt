package com.example.togetherpet.searching

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
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

            /*override fun getPosition(): LatLng {
                return super.getPosition()
            }*/
        })



        return binding.root
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