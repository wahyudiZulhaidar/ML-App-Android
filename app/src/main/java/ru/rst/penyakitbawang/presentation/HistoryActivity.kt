package ru.rst.penyakitbawang.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.rst.penyakitbawang.databinding.ActivityHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = HistoryAdapter { item ->
            val intent = Intent(this@HistoryActivity, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_IMAGE_URI, item.imagePath)
                putExtra(ResultActivity.EXTRA_RESULT, item.result)
                putExtra(ResultActivity.EXTRA_SCORE, item.score)
            }
            startActivity(intent)
        }

        binding.rvListEvents.layoutManager = LinearLayoutManager(this)
        binding.rvListEvents.adapter = adapter

        viewModel.getDataHistory.observe(this) { data ->
            adapter.submitList(data)
        }
    }
}