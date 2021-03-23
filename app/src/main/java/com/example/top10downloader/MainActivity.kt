package com.example.top10downloader

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml

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

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: parameter is $result")
            }

        }

        private fun downloadXml(urlPath: String?): String {

            val xmlResult = StringBuilder()

            try {

                val url = URL(urlPath)

                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                val response = connection.responseCode

                Log.d(TAG, "downloadXml: The response code $response")

//                val inputStream = connection.inputStream
//
//                val inputStreamReader = InputStreamReader(inputStream)
//
//                val bufferedReader = BufferedReader(inputStreamReader)

                val reader = BufferedReader(InputStreamReader(connection.inputStream))

                val inputBuffer = CharArray(500)

                var charsRead = 0

                while (charsRead >= 0) {

                    charsRead = reader.read(inputBuffer)

                    if (charsRead > 0) {

                        xmlResult.append(String(inputBuffer, 0, charsRead))

                    }

                }

                reader.close()

                Log.d(TAG, "downloadXml: Recieved ${xmlResult.length} bytes")

                return xmlResult.toString()

            } catch (e: MalformedURLException) {

                Log.e(TAG, "downloadXml: Invalid URL ${e.message}", )

            } catch (e: IOException) {

                Log.e(TAG, "downloadXml: IO Exception reading data: ${e.message}")

            } catch (e: SecurityException) {

                Log.e(TAG, "downloadXml: security exception: ${e.message}")

            }catch (e: Exception) {

                Log.e(TAG, "downloadXml: Unknown Error: ${e.message}")

            }

            return  "" // if it gets to here theres been a problem return an empty string

        }
    }


}