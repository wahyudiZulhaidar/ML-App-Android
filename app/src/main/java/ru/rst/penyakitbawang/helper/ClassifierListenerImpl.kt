package ru.rst.penyakitbawang.helper

import android.util.Log
import android.widget.Toast
import org.tensorflow.lite.task.vision.classifier.Classifications
import javax.inject.Inject
import ru.rst.penyakitbawang.MainActivity.Companion

class ClassifierListenerImpl @Inject constructor() : ClassifierListener {
    override fun onError(error: String) {
    }

    override fun onResults(results: List<Classifications>?) {
    }
}
