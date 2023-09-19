package com.faris.storyapp.ui.activities.addstory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import com.faris.storyapp.R
import com.faris.storyapp.databinding.FragmentAddStoryBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.utils.Utils
import com.faris.storyapp.data.Result
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalPagingApi::class)
@RequiresApi(Build.VERSION_CODES.M)
class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private var getFile: File? = null
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupButtonEnabled()
    }

    private fun setupView() {
        with(binding) {
            btnAddImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions.launch(arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ))
                } else if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                ) {
                    showBottomSheetDialogAddImage()
                }
            }
            btnUpload.setOnClickListener {
                observerViewModel()
            }
            toolbarAddStory.setOnClickListener {
                view?.findNavController()?.popBackStack()
            }
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("LOG_TAG", "${it.key} = ${it.value}")
            }
        }

    private fun setupButtonEnabled() {
        with(binding) {
            val desc = edtDesc.text
            btnUpload.isEnabled = desc != null
        }
    }

    private fun observerViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val addStoryViewModel: AddStoryViewModel by viewModels { factory }
        addStoryViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            with(binding) {
                println("ada token tidak : $token")
                if (token != null) {
                    val file = Utils.reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val image: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    val desc = edtDesc.text.toString().toRequestBody("text/plain".toMediaType())
                    addStoryViewModel.addStory(token, image, desc)
                        .observe(requireActivity()) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        progressBar.visibility = View.VISIBLE
                                    }
                                    is Result.Success -> {
                                        view?.findNavController()
                                            ?.navigate(R.id.action_addStoryFragment_to_homeFragment)
                                    }
                                    is Result.Error -> {
                                        Toast.makeText(requireContext(),
                                            "Harap coba lagi.",
                                            Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showBottomSheetDialogAddImage() {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(R.layout.bottom_sheet_add_image)
        val btnCamera = dialog.findViewById<RelativeLayout>(R.id.rl_camera)
        val btnGallery = dialog.findViewById<RelativeLayout>(R.id.rl_gallery)
        dialog.show()

        btnCamera?.setOnClickListener {
            startCamera()
            dialog.dismiss()
        }

        btnGallery?.setOnClickListener {
            startGallery()
            dialog.dismiss()
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        Utils.createCustomTempFile(requireActivity()).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.faris.storyapp.ui.activities.addstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val myFile = File(currentPhotoPath!!)
            getFile = myFile
            val ei = ExifInterface(getFile!!.path)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
            val data = BitmapFactory.decodeFile(getFile?.path)
            with(binding) {
                if (getFile != null) {
                    btnAddImage.visibility = View.GONE
                }
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        ivPreview.setImageBitmap(Utils.rotate(data, 90f))
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        ivPreview.setImageBitmap(Utils.rotate(data, 180f))
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        ivPreview.setImageBitmap(Utils.rotate(data, 270f))
                    }
                    ExifInterface.ORIENTATION_NORMAL -> {
                        ivPreview.setImageBitmap(data)
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedImg: Uri = it.data?.data!!
                val myFile = Utils.uriToFile(selectedImg, requireActivity())
                getFile = myFile
                val data = Uri.fromFile(myFile)
                binding.ivPreview.setImageURI(data)
                if (getFile != null) {
                    binding.btnAddImage.visibility = View.GONE
                }
            }
        }
}