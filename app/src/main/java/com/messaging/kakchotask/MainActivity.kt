package com.messaging.kakchotask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messaging.kakchotask.adapters.IconListAdapter
import com.messaging.kakchotask.model.Icon
import com.messaging.kakchotask.utils.DownloadService
import com.messaging.kakchotask.utils.NUMBER_OF_ICONS
import com.messaging.kakchotask.utils.QUERY
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.isLoading
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.isNetworkConnected
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.toast
import com.messaging.kakchotask.utils.START_INDEX
import com.messaging.kakchotask.viewModel.OurViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: IconListAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var viewModel: OurViewModel
    private lateinit var icon_list:RecyclerView
    private lateinit var progress_bar:ProgressBar
    var query = "iconsets"
    val defaultQuery ="iconsets"

    private var startIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        icon_list=findViewById(R.id.icon_list)
        progress_bar=findViewById(R.id.progress_bar)
        setSupportActionBar(toolbar)
        title = ""

        init()

        viewModel = ViewModelProvider(this).get(OurViewModel::class.java)

        if (isNetworkConnected(this))
            loadData(query, NUMBER_OF_ICONS, startIndex)
        else
            toast("No internet connection available")

        addOnScrollListener()
    }

    private fun init() {
        showLoading(false)
        adapter = IconListAdapter(listOf())
        layoutManager = GridLayoutManager(this, 2)

        with(icon_list) {
            layoutManager = this@MainActivity.layoutManager
            adapter = this@MainActivity.adapter
        }


    }

    private fun loadData(query: String, count: Int, index: Int) {
        showLoading(true)
        viewModel.getIcons(query, count, index).observe(this,
            Observer<List<Icon>> { list ->

                listItems.addAll(list)
                removeDuplicateValues(listItems)
                adapter.submitList(listItems)

                showLoading(false)
                if (list.isEmpty()) toast("No more results found!")
                Log.d("Main", listItems.size.toString())
            })
    }

    private fun removeDuplicateValues(items: List<Icon>) {
        val map = LinkedHashMap<Int, Icon>()

        for (item in items) {
            map[item.id] = item
        }
        listItems.clear()
        listItems.addAll(map.values)
    }

    private fun addOnScrollListener() {
        icon_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val count = layoutManager.itemCount

                if (dy > 0 && !isLoading) {
                    val holderCount = layoutManager.childCount
                    val oldCount = layoutManager.findFirstVisibleItemPosition()

                    if (holderCount + oldCount >= count - 4 && !isLoading) {
                        startIndex += 20
                        viewModel.getIcons(query, NUMBER_OF_ICONS, startIndex)
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(QUERY, query)
        outState.putInt(START_INDEX, startIndex)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as SearchView

        // To handle when back button is clicked on the toolbar
        search.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                if (query != defaultQuery) { // Something has been searched by the user
                    removeAndReload()
                    viewModel.getIcons(defaultQuery, NUMBER_OF_ICONS, 0)
                }
                return true
            }

        })

        // To handle when search query is submitted
        // been used to search the query as the user types. For now search is only made
        // when user submits using search button on keyboard
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false

                removeAndReload()
                this@MainActivity.query = query
                viewModel.getIcons(query, NUMBER_OF_ICONS, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                if(newText==null) return false
                removeAndReload()
                if(newText.isEmpty()){
                    this@MainActivity.query = defaultQuery
                    viewModel.getIcons(defaultQuery, NUMBER_OF_ICONS, 0)
                }
                else{
                this@MainActivity.query = newText
                viewModel.getIcons(newText, NUMBER_OF_ICONS, 0)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun removeAndReload() {
        listItems.clear()
        adapter.submitList(listOf())
        showLoading(true)
    }

    private fun showLoading(boolean: Boolean) {
        if (boolean) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, DownloadService::class.java)
        stopService(intent)
    }

    companion object {
        private val listItems = mutableListOf<Icon>()
    // Could be made non-static and be preserved
        // with onSaveInstanceState()
    }
}