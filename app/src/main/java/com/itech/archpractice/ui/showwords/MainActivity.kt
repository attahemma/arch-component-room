package com.itech.archpractice.ui.showwords

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itech.archpractice.R
import com.itech.archpractice.WordsApplication
import com.itech.archpractice.data.model.Word
import com.itech.archpractice.ui.adapters.WordListAdapter
import com.itech.archpractice.ui.addword.NewWordActivity

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView : RecyclerView
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    private val newWordActivityRequestCode = 1

    private lateinit var wordListAdapter: WordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        setupDisplay()

        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { wordListAdapter.submitList(it) }
        })

    }

    private fun setupDisplay(){
        wordListAdapter = WordListAdapter()
        recyclerView.adapter = wordListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Word(it)
                wordViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }
}