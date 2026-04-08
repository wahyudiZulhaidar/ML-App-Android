package ru.rst.penyakitbawang.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import ru.rst.penyakitbawang.database.DataHistoryDatabase
import ru.rst.penyakitbawang.database.DataHistoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import javax.inject.Inject


class ImageClassifierHelper @Inject constructor(
    @ApplicationContext val context: Context,
    val db: DataHistoryDatabase,
    var classifierListener: ClassifierListener
) {

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(DEFAULT_THRESHOLD)
            .setMaxResults(DEFAULT_MAX_RESULTS)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(THREAD_COUNT)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                MODEL_NAME,
                optionsBuilder.build()
            )
        } catch (e: Exception) {
            classifierListener.onError("Failed to initialize classifier: ${e.message}")
            Log.e(TAG, "Classifier initialization error", e)
        }
    }

    fun classifyStaticImage(imageUri: Uri, currentImageUri: Uri?) {
        if (imageUri == Uri.EMPTY) {
            classifierListener.onError("Invalid image URI")
            return
        }

        val bitmap = toBitmap(imageUri)
        if (bitmap == null) {
            classifierListener.onError("Failed to convert image to Bitmap")
            return
        }

        val tensorImage = try {
            val imageProcessor = ImageProcessor.Builder()
                // 1. Ubah ukuran ke 224x224 agar sesuai dengan model di Netron
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))

                // 2. Normalisasi akan membagi nilai piksel dengan 255.0f
                //    dan otomatis mengubah tipe data tensor menjadi FLOAT32.
                .add(NormalizeOp(0f, 255.0f))

                // 3. HAPUS baris .add(CastOp(DataType.UINT8))
                //    karena model meminta float32, bukan uint8.
                .build()

            // Gunakan DataType.FLOAT32 saat inisialisasi TensorImage
            val tensor = TensorImage(DataType.FLOAT32)
            tensor.load(bitmap)
            imageProcessor.process(tensor)

        } catch (e: Exception) {
            classifierListener.onError("Failed to preprocess image: ${e.message}")
            Log.e(TAG, "Image preprocessing error", e)
            return
        }

        try {
            val results = imageClassifier?.classify(tensorImage)
            classifierListener.onResults(results)
            results?.let {
                if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                    val category = it[0].categories.maxByOrNull { c -> c.score }
                    category?.let { data ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val entity = DataHistoryEntity(
                                imagePath = currentImageUri.toString(),
                                result = data.label,
                                score = data.score
                            )
                            db.dataHistoryDao().insert(entity)
                            db.dataHistoryDao().deleteOldHistory()
                        }
                    }
                } else {
                    classifierListener.onError("No classification results found.")
                }
            }
        } catch (e: Exception) {
            classifierListener.onError("Classification error: ${e.message}")
            Log.e(TAG, "Classification failed", e)
        }
    }
    @Suppress("DEPRECATION")
    private fun toBitmap(imageUri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    .copy(Bitmap.Config.ARGB_8888, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to convert URI to Bitmap", e)
            null
        }
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
        private const val MODEL_NAME = "final_model_mobilenetv2_bawang_merah_quant.tflite"
        private const val DEFAULT_THRESHOLD = 0.1f
        private const val DEFAULT_MAX_RESULTS = 1
        private const val THREAD_COUNT = 4
    }
}