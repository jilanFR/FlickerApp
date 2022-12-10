package com.example.trial

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.trial.Constants.TAG_MAIN_ACTIVITY
import com.example.trial.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() , RVAdapter.ImgRowClickListener {

    // Get user input and use as the tag for the search
    lateinit var userInputTag :EditText
    lateinit var userPerPage :EditText

    private lateinit var url :String

    private lateinit var binding: ActivityMainBinding
    private lateinit var images: ArrayList<PhotoItems>
    private lateinit var RVAdapter:RVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup binding to use
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        // Initialize images array list
        images = arrayListOf()

        RVAdapter = RVAdapter(this@MainActivity, images,this)
        binding.recyclerView.adapter = RVAdapter
       // binding.recyclerView.layoutManager = GridLayoutManager(this,2)

        binding.mainImg.setOnClickListener {
            updateVisibility(false,"")
        }

        userInputTag = binding.userInput
        userPerPage = binding.perPage

        binding.btn.setOnClickListener {
            if (userInputTag.text.isNotEmpty() && userPerPage.text.isNotEmpty() ) {

                // Search the url using the tag
                url = "https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=${Constants.APIKEY}&tags=${userInputTag.text}&per_page=${userPerPage.text}&format=json&nojsoncallback=1"

                requestAPI(url)
            }else{
                Toast.makeText(this, "please fill in the search fields : " , Toast.LENGTH_LONG).show()
            }
        }
    }//END OF ON CREATE


    // Create coroutine
    private fun requestAPI(url:String){
        CoroutineScope(Dispatchers.IO).launch{
            var response:String?
            try{
                response=URL(url).readText()
                async { GettingPhotos(response!!) }.await()

            }catch (e: Exception){
                response=null
                println("Error: $e")
                Log.d(TAG_MAIN_ACTIVITY, "requestAPI: $images")
            }
        }
    }

    // "SUSPEND" waits for info to be gathered before doing anything else
    suspend fun GettingPhotos(result: String) {

        withContext(Dispatchers.Main) {
            var jsonObj = JSONObject(result)
            var photos = jsonObj.getJSONObject("photos").getJSONArray("photo")

            /* Extracting JSON returns from the API */
            for (i in 0 until photos.length()) {
                var id = photos.getJSONObject(i).getString("id")
                var server = photos.getJSONObject(i).getString("server")
                var title = photos.getJSONObject(i).getString("title")
                var secrete = photos.getJSONObject(i).getString("secret")


                    var photoItem = PhotoItems(id, secrete, server, title)
                    images.add(photoItem)
                }


                    RVAdapter.loadNewData(images)
                }

    }

    private fun updateVisibility(showImg: Boolean, img: String) {
        if (showImg) {
            binding.mainImg.isVisible = true
            binding.recyclerView.isVisible = false

            // make image display
            Glide.with(this)
                .load(img)
                .into(binding.mainImg)

        } else {
            binding.recyclerView.isVisible = true
            binding.mainImg.isVisible = false
        }
    }

    override fun onClick(img: String) {

        updateVisibility(true,img)

    }

}