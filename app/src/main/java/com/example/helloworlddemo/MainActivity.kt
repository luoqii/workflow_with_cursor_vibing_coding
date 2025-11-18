package com.example.helloworlddemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworlddemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val greetingFormatter = GreetingFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.displayButton.setOnClickListener {
            val message = greetingFormatter.formatDefaultGreeting(
                getString(R.string.hello_message)
            )
            binding.messageText.text = message
        }
    }
}
