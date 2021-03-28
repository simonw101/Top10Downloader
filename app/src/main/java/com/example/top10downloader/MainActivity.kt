package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBar
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
       return  """
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

    private val downloadData by lazy {  DownLoadData(this, binding.xmlListView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate:  called")

        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")

        Log.d(TAG, "onCreate:  done")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {

        private class DownLoadData(context: Context, listview: ListView) : AsyncTask<String, Void, String>() {

            private val TAG = "DownLoadData"

            var propContext : Context by Delegates.notNull()

            var propListView : ListView by Delegates.notNull()

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