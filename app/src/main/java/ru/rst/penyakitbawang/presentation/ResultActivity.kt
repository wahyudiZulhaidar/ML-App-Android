package ru.rst.penyakitbawang.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import ru.rst.penyakitbawang.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val result = intent.getStringExtra(EXTRA_RESULT)
        val scoreFloat = intent.getFloatExtra(EXTRA_SCORE, 0F)

        val imageUri = imageUriString?.toUri()

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = "$result ${"%.2f".format(scoreFloat * 100)}%"
    }

    companion object {
        const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        const val EXTRA_RESULT = "EXTRA_RESULT"
        const val EXTRA_SCORE = "EXTRA_SCORE"
    }
}