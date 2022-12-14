package ru.netology.nmedia

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.util.hideKeyboard
import ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        run {
            val preferences = getPreferences(Context.MODE_PRIVATE)
            preferences.edit{
                putString("key", "value")
            }
        }

        run{
            val preferences = getPreferences(Context.MODE_PRIVATE)
            val value = preferences.getString("key", "no value") ?: return@run
            Snackbar.make(binding.root, value, Snackbar.LENGTH_INDEFINITE).show()
        }

        val adapter = PostsAdapter(viewModel)

        binding.postsContainer.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.fag.setOnClickListener {
            viewModel.onAddClicked()
        }

        val postContentActivityLauncher = registerForActivityResult(
            PostContentActivity.ResultContract
        ) { postContent ->
            if (postContent == null) {
                viewModel.currentPost.value = null
                return@registerForActivityResult
            }
            viewModel.onSaveButtonClicked(postContent)
        }

        viewModel.navigateToPostContentScreenEvent.observe(this) {
            postContentActivityLauncher.launch(viewModel.currentPost.value?.content)
        }

        viewModel.playVideoPost.observe(this) {
            val playVideo = viewModel.playVideoPost.value?.video ?: return@observe
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playVideo))
            startActivity(intent)
        }
    }
}