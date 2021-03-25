package com.example.top10downloader

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.net.URL

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate:  called")

        val downloadData = DownLoadData()

        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")

        Log.d(TAG, "onCreate:  done")
    }

    companion object {

        private class DownLoadData : AsyncTask<String, Void, String>() {

            private val TAG = "DownLoadData"

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

            }

        }

        private fun downloadXml(urlPath: String?): String {
            return URL(urlPath).readText()
        }

    }


}