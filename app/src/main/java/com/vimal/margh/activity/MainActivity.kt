package com.vimal.margh.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.vimal.margh.R
import com.vimal.margh.adapter.AdapterWallpaper
import com.vimal.margh.callback.CallbackWallpaper
import com.vimal.margh.databinding.ActivityMainBinding
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.rest.RestAdapter.createAPI
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Constant.EXTRA_KEY
import com.vimal.margh.util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), AdapterWallpaper.OnItemClickListener {
    private val modelLists: MutableList<ModelWallpaper?> = ArrayList()
    private var category: String = "cars"
    private var adapterList: AdapterWallpaper? = null
    private var callbackCall: Call<CallbackWallpaper?>? = null
    private var postTotal = 0
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.included.pvProgress.visibility = View.VISIBLE

        requestAction(1)

        binding!!.rvRecycler.layoutManager = GridLayoutManager(this, 3)
        adapterList = AdapterWallpaper(this, binding!!.rvRecycler, modelLists)
        binding!!.rvRecycler.adapter = adapterList

        adapterList!!.setOnLoadMoreListener(object : AdapterWallpaper.OnLoadMoreListener {
            override fun onLoadMore(int: Int) {
                try {
                    if (postTotal > adapterList!!.itemCount && int != 0) {
                        val nextPage = int + 1
                        requestAction(nextPage)
                    } else {
                        adapterList!!.setLoaded()
                    }
                } catch (e: Exception) {
                    Utils.getErrors(e)
                }
            }
        })


        adapterList!!.setOnItemClickListener(this)
        binding!!.slSwipe.setColorSchemeResources(
            R.color.color_orange,
            R.color.color_red,
            R.color.color_blue,
            R.color.color_green
        )
        binding!!.slSwipe.setOnRefreshListener {
            if (callbackCall != null && callbackCall!!.isExecuted) callbackCall!!.cancel()
            requestAction(1)
        }

        binding!!.included.btError.setOnClickListener {
            requestAction(1)
            binding!!.included.llError.visibility = View.GONE
            binding!!.rvRecycler.visibility = View.GONE
            binding!!.included.pvProgress.visibility = View.VISIBLE
        }

    }


    private fun requestListPostApi(pageNo: Int) {
        val apiInterface = createAPI(this)
        callbackCall = apiInterface.getWallpapers(category, "vertical", Constant.LOAD_MORE, pageNo)
        callbackCall!!.enqueue(object : Callback<CallbackWallpaper?> {
            override fun onResponse(
                call: Call<CallbackWallpaper?>,
                response: Response<CallbackWallpaper?>
            ) {
                binding!!.included.pvProgress.visibility = View.GONE

                binding!!.included.llError.visibility = View.GONE
                binding!!.rvRecycler.visibility = View.VISIBLE
                binding!!.slSwipe.isRefreshing = false
                val resp = response.body()
                if (resp != null && resp.totalHits == 500) {
                    postTotal = resp.total
                    displayApiResult(resp.hits)
                } else {
                    binding!!.included.pvProgress.visibility = View.GONE
                    binding!!.slSwipe.isRefreshing = false
                    binding!!.included.llError.visibility = View.VISIBLE
                    binding!!.included.tvError.setText(R.string.no_data_available)
                    binding!!.included.ivError.setImageResource(R.drawable.ic_no_score)
                }
            }

            override fun onFailure(call: Call<CallbackWallpaper?>, t: Throwable) {
                if (!call.isCanceled) {
                    binding!!.slSwipe.isRefreshing = false
                    binding!!.included.pvProgress.visibility = View.GONE
                    binding!!.included.llError.visibility = View.VISIBLE
                    binding!!.included.ivError.setImageResource(R.drawable.ic_no_network)
                    binding!!.included.tvError.setText(R.string.no_internet_connection)
                }
            }
        })
    }


    private fun displayApiResult(modelLists: List<ModelWallpaper?>) {
        try {

            adapterList!!.insertData(modelLists)
            binding!!.slSwipe.isRefreshing = false
            if (modelLists.isEmpty()) {
                binding!!.included.llError.visibility = View.VISIBLE
                binding!!.included.tvError.setText(R.string.no_data_available)
            }
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }


    private fun requestAction(pageNo: Int) {
        try {
            if (pageNo != 1) {
                adapterList!!.setLoading()
            }
            Handler().postDelayed({ requestListPostApi(pageNo) }, Constant.DELAY_TIME)
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.slSwipe.isRefreshing = false
        if (callbackCall != null && callbackCall!!.isExecuted) {
            callbackCall!!.cancel()
        }
    }


    override fun onItemClick(modelWallpaper: ModelWallpaper?) {
        try {
            val intent = Intent(this, ActivityDetail::class.java)
            intent.putExtra(EXTRA_KEY, modelWallpaper)
            startActivity(intent)
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }

    override fun onItemDelete(modelWallpaper: ModelWallpaper?) {
        Toast.makeText(this, "Added to Favorite", Toast.LENGTH_SHORT).show()
    }
}

