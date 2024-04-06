package com.jawadkhansahil.groupie

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jawadkhansahil.groupie.adapter.SearchUserAdapter
import com.jawadkhansahil.groupie.databinding.ActivitySearchBinding
import com.jawadkhansahil.groupie.models.UserModel
import com.jawadkhansahil.groupie.utils.FirebaseUtil


class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    var adapter: SearchUserAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            closeKeyboard()
            finish()
        }

        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text.toString()
            if (searchText.isNotEmpty() && searchText.length > 3){
                filterList(searchText)
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val searchText = binding.searchEditText.text.toString()

                if (searchText.isNotEmpty() && searchText.length > 3) {
                    filterList(searchText)
                }
                true
            } else {
                false
            }
        }

        binding.searchEditText.requestFocus()
       showKeyboard()

    }
    private fun showKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun filterList(searchText: String) {
        val query = FirebaseUtil.allUserCollectionReference()
            .orderBy("userName")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        adapter = SearchUserAdapter(options, this@SearchActivity)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.searchRecyclerView.adapter = adapter
        adapter!!.startListening()
    }


    override fun onStart() {
        super.onStart()
        if (adapter != null){
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null){
            adapter!!.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null){
            adapter!!.startListening()
        }
    }
}