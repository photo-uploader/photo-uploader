package com.example.photouploader.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photouploader.R
import com.example.photouploader.data.Item
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class UploadFragment : NavigatorFragment(R.layout.fragment_upload) {

    private val viewModel: UploadViewModel by viewModels()

    private lateinit var ivPreview: ImageView
    private lateinit var pbProgress: ProgressBar
    private lateinit var bUpload: Button

    private val hasPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.iv_preview)
        pbProgress = view.findViewById(R.id.pb_progress)
        bUpload = view.findViewById(R.id.b_upload_item)

        view.findViewById<Button>(R.id.b_select_image).setOnClickListener {
            if (hasPermission) {
                openGallery()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RC_STORAGE_PERMISSIONS
                )
            }
        }

        bUpload.setOnClickListener {
            viewModel.uploadItem()
        }

        view.findViewById<EditText>(R.id.et_tag).addTextChangedListener {
            viewModel.tag.value = it.toString()
        }

        view.findViewById<CheckBox>(R.id.cb_defect).setOnCheckedChangeListener { _, isChecked ->
            viewModel.isDefect.value = isChecked
        }

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            ivPreview.setImageURI(uri)
            bUpload.isEnabled = uri != null
        }

        viewModel.isProgressVisible.observe(viewLifecycleOwner) {
            pbProgress.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isUploadFinished.observe(viewLifecycleOwner) {
            if (it) navigator.pop()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_STORAGE_PERMISSIONS) {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_IMAGE_PICK_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICK_RESULT && resultCode == Activity.RESULT_OK && data != null) {
            viewModel.imageUri.value = data.data
        }
    }

    companion object {
        const val RC_STORAGE_PERMISSIONS = 111
        const val RC_IMAGE_PICK_RESULT = 222
    }

}

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val imageUri = MutableLiveData<Uri?>()
    val tag = MutableLiveData<String>()
    val isDefect = MutableLiveData<Boolean>()

    val isProgressVisible = MutableLiveData(false)
    val isUploadFinished = MutableLiveData(false)

    fun uploadItem() {
        isProgressVisible.value = true
        imageUri.value?.let {
            val item = Item(
                imageUri = it,
                tag = tag.value ?: "",
                isDefect = isDefect.value ?: false
            )
            repository.uploadItem(item) {
                isProgressVisible.value = false
                isUploadFinished.value = true
            }
        }
    }
}