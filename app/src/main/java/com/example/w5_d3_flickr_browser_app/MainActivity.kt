package com.example.w5_d3_flickr_browser_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var images: ArrayList<Image>

    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    lateinit var etxt: EditText
    lateinit var button: Button

    private lateinit var llBottom: LinearLayout
    private lateinit var ivMain: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etxt = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        var search_category = etxt.text.toString()


        images = arrayListOf()

        rvMain = findViewById(R.id.recyclerView)
        rvAdapter = RVAdapter(this, images)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        llBottom = findViewById(R.id.linearLayout)
        button.setOnClickListener {
            if(etxt.text.isNotEmpty()){
                requestAPI()
            }else{
                Toast.makeText(this, "Search field is empty", Toast.LENGTH_LONG).show()
            }
        }

        ivMain = findViewById(R.id.ivThumbnail)
        ivMain.setOnClickListener { closeImg() }

    }

    private fun requestAPI(){
        CoroutineScope(IO).launch {
            val data = async { getPhotos() }.await()
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getPhotos(): String{
        var response = ""
        try{
            response = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&per_page=10&api_key=cb0cbca5c50568f7e3189b08d8e6a89b&tags=${etSearch.text}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("ISSUE: $e")
        }
        return response
    }

    private suspend fun showPhotos(data: String){
//        https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        withContext(Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                images.add(Image(title, photoLink))
            }
            rvAdapter.notifyDataSetChanged()
        }
    }

    fun openImg(link: String){
        Glide.with(this).load(link).into(ivMain)
        ivMain.isVisible = true
        rvMain.isVisible = false
        llBottom.isVisible = false
    }

    private fun closeImg(){
        ivMain.isVisible = false
        rvMain.isVisible = true
        llBottom.isVisible = true
    }
}