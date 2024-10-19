package com.example.togetherpet.searching

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.togetherpet.utils.DpUtils
import com.example.togetherpet.databinding.MissingBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MissingBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: MissingBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MissingBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateBottomSheet(
        petName: String,
        species: String,
        age: String,
        missingPlace: String,
        addInfo: String,
        url: String
    ) {
        if (_binding != null && isAdded) {
            _binding?.let {
                it.missingBottomPetName.text = petName
                it.missingBottomSpeciesText.text = species
                it.missingBottomAgeText.text = age
                it.missingBottomMissingPlaceText.text = missingPlace
                it.missingBottomAddInfo.text = addInfo

                Glide.with(this)
                    .load(url)
                    .apply(
                        RequestOptions().centerCrop()
                            .transform(RoundedCorners(DpUtils.dpToPx(requireContext(), 10)))
                    )
                    .into(it.missingBottomPetImg)

                Log.d("yeong", "bottom sheet 설정")
            }
        }
    }
}