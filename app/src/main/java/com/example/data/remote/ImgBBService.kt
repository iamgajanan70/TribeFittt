package com.example.data.remote

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class ImgBBResult(
    val success: Boolean,
    val url: String? = null,
    val displayUrl: String? = null,
    val deleteUrl: String? = null,
    val imageHash: String? = null,
    val errorMessage: String? = null
)

class ImgBBService {
    suspend fun uploadBase64Image(base64Image: String, apiKey: String): ImgBBResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "your_imgbb_free_tier_api_key") {
            return@withContext ImgBBResult(
                success = false,
                errorMessage = "ImgBB API Key is not configured in Secrets panel."
            )
        }

        try {
            val urlString = "https://api.imgbb.com/1/upload?key=${URLEncoder.encode(apiKey, "UTF-8")}"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            // Remove header prefix if present (e.g. "data:image/png;base64,")
            val cleanBase64 = if (base64Image.contains(",")) {
                base64Image.substringAfter(",")
            } else {
                base64Image
            }

            val postData = "image=" + URLEncoder.encode(cleanBase64, "UTF-8")

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(postData)
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            val json = JSONObject(response.toString())
            if (json.optBoolean("success", false)) {
                val data = json.getJSONObject("data")
                val imageUrl = data.optString("url")
                val displayUrl = data.optString("display_url")
                val deleteUrl = data.optString("delete_url")
                val hash = data.optString("id")

                ImgBBResult(
                    success = true,
                    url = imageUrl,
                    displayUrl = displayUrl,
                    deleteUrl = deleteUrl,
                    imageHash = hash
                )
            } else {
                val errorMsg = json.optJSONObject("error")?.optString("message") ?: "Upload failed"
                ImgBBResult(success = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            ImgBBResult(success = false, errorMessage = e.localizedMessage ?: "Network error")
        }
    }
}
