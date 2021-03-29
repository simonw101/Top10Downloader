package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.example.top10downloader.databinding.ActivityMainBinding
import java.net.URL
import kotlin.properties.Delegates

private const val TAG = "MainActivity"

class FeedEntry {

    var name: String = ""
    var artist: String = ""
    var realeaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name: $name
            artist: $artist
            realease Date: $realeaseDate
            summary: $summary
            url: $imageURL
            
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadData: DownLoadData? = null

    private var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"

    private var feedLimit = 10

    private var feedCachedURL = "INVALIDATED"

    private val STATE_URL = "feedurl"

    private val STATE_LIMIT = "feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: called")

        if (savedInstanceState != null) {

            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)

        }

        downloadUrl(feedUrl.format(feedLimit))

        Log.d(TAG, "onCreate: done")
    }

    private fun downloadUrl(feedUrl: String) {

        if (feedUrl != feedCachedURL) {

            Log.d(TAG, "downloadUrl: starting Async Task")
            downloadData = DownLoadData(this, binding.xmlListView)
            downloadData?.execute(feedUrl)
            feedCachedURL = feedUrl
            Log.d(TAG, "downloadUrl: done")

        } else {

            Log.d(TAG, "downloadUrl: - URL not changed")

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {

            menu?.findItem(R.id.mnu10)?.isChecked = true

        } else {

            menu?.findItem(R.id.mnu25)?.isChecked = true

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.mnuFree ->

                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"

            R.id.mnuPaid -> {

                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"

            }

            R.id.mnuSongs -> {

                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"

            }

            R.id.mnu10, R.id.mnu25 -> {

                if (!item.isChecked) {

                    item.isChecked = true

                    feedLimit = 35 - feedLimit

                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feed Limit to $feedLimit")

                } else {

                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feed Limit to unchained")

                }

            }

            R.id.mnuRefresh -> feedCachedURL = "INVALIDATED"

            else ->

                return super.onOptionsItemSelected(item)

        }

        downloadUrl(feedUrl.format(feedLimit))

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_URL,  feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)

    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {

        private class DownLoadData(context: Context, listview: ListView) : AsyncTask<String, Void, String>() {

            private val TAG = "DownLoadData"

            var propContext: Context by Delegates.notNull()

            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listview
            }

            override fun doInBackground(vararg params: String?): String {
                Log.d(TAG, "doInBackground: starts with ${params[0]}")

                val rssFeed = downloadXml(params[0])

                if (rssFeed.isEmpty()) {

                    Log.e(TAG, "doInBackground: Error downloading")

                }

                return rssFeed
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(TAG, "onPostExecute: parameter is $result")
                val parseApplications = ParseApplications()

                parseApplications.parse(result)

//                val arrayAdapter = ArrayAdapter<FeedEntry>(propContext, R.layout.list_item, parseApplications.applications)
//
//                propListView.adapter = arrayAdapter

                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)

                propListView.adapter = feedAdapter

            }

        }

        private fun downloadXml(urlPath: String?): String {
            return URL(urlPath).readText()
        }

    }


}