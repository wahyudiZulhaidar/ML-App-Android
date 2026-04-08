package ru.rst.penyakitbawang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.rst.penyakitbawang.databinding.ActivityMainBinding
import ru.rst.penyakitbawang.helper.ClassifierListener
import ru.rst.penyakitbawang.helper.ClassifierListenerImpl
import ru.rst.penyakitbawang.helper.ImageClassifierHelper
import ru.rst.penyakitbawang.presentation.HistoryActivity
import ru.rst.penyakitbawang.presentation.ResultActivity
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var imageClassifierHelper: ImageClassifierHelper
    @Inject
    lateinit var classifierListener : ClassifierListenerImpl

    private var currentImageUri: Uri? = null
    private var result: String? = null
    private var confidence: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper.classifierListener  = object : ClassifierListener {
            override fun onError(error: String) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    showToast(error)
                }
            }

            override fun onResults(results: List<Classifications>?) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    results?.let {
                        val bestCategory = it.firstOrNull()?.categories?.maxByOrNull { category -> category.score }
                        bestCategory?.let { category ->
                            result = category.label
                            confidence = category.score
                            moveToResult()
                        }
                    } ?: showToast("No results found")
                }
            }
        }

        binding.historyButton.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            if (currentImageUri != null) {
                analyzeImage(currentImageUri!!)
            } else {
                showToast("Silakan pilih gambar terlebih dahulu")
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast("Tidak ada gambar yang dipilih")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val uCropResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { uri ->
                currentImageUri = uri
                showImage()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val uCropError = UCrop.getError(result.data!!)
            uCropError?.let {
                when (it) {
                    is IOException -> Log.e(UCROP, "Input/output error: ${it.message}", it)
                    is IllegalArgumentException -> Log.e(UCROP, "Invalid argument: ${it.message}", it)
                    else -> Log.e(UCROP, "Unknown error: ${it.message}", it)
                }
            }
            showToast("Kesalahan saat memproses gambar")
        }
    }

    private fun uCrop(imageUri: Uri) {
        val destinationUri = Uri.fromFile(File(getExternalFilesDir(null), "cropped_image_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.md_theme_primary))
            setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.md_theme_secondary))
            setActiveControlsWidgetColor(ContextCompat.getColor(this@MainActivity, R.color.md_theme_onPrimary))
            setToolbarWidgetColor(ContextCompat.getColor(this@MainActivity, R.color.md_theme_onPrimary))
        }

        val startUCrop = UCrop.of(imageUri, destinationUri)
            .withOptions(options)
            .getIntent(this@MainActivity)

        uCropResult.launch(startUCrop)
        Log.d(UCROP, "Image URI: $imageUri")
    }

    private fun analyzeImage(imageUri: Uri) {
        binding.progressIndicator.visibility = View.VISIBLE
        Log.d(TAG, "Analyzing image URI: $imageUri")

        imageClassifierHelper.classifyStaticImage(imageUri, currentImageUri)
    }

    private fun moveToResult() {
        val intent = Intent(this@MainActivity, ResultActivity::class.java)
        intent.putExtra(EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra(EXTRA_RESULT, result)
        intent.putExtra(EXTRA_SCORE, confidence)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val UCROP = "uCropLogging"
        private const val TAG = "MainActivity"
        private const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        private const val EXTRA_RESULT = "EXTRA_RESULT"
        private const val EXTRA_SCORE = "EXTRA_SCORE"
    }
}