package com.hdcutter.bgremover

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hdcutter.bgremover.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Launcher to pick an image from the gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Pass the image URI to the processing screen
            val intent = Intent(this, ProcessActivity::class.java).apply {
                putExtra(ProcessActivity.EXTRA_IMAGE_URI, it.toString())
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickImage.setOnClickListener {
            // Open image picker — supports all image formats
            pickImageLauncher.launch("image/*")
        }
    }
}
