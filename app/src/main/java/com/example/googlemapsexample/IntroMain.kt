package com.example.googlemapsexample

import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.googlemapsexample.Models.IntroScreen
import com.example.googlemapsexample.Models.IntroScreenAdapter
import kotlinx.android.synthetic.main.activity_intro_screen.*

class IntroMain: AppCompatActivity() {
    private val introSliderAdapter=IntroScreenAdapter(
        listOf(
            IntroScreen(
                "Sunlight",
            "Go for the exciting ideas!",
                      R.drawable.swooshicon),
            IntroScreen(
                    "Sunlight",
               "Go for the exciting ideas!",
            R.drawable.swooshicon),
            IntroScreen(
                "Sunlight",
           "Go for the exciting ideas!",
                R.drawable.swooshicon)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)
        introSliderViewPager2.adapter=introSliderAdapter
        setupIndicators()
        setcurrentIndicator(0)
        introSliderViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setcurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(){
        val indicators= arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams:LinearLayout.LayoutParams=LinearLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT)

        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){
            indicators[i]=ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.indicator_inactive))
                this?.layoutParams=layoutParams
            }
            introViewPagerLinearLayout.addView(indicators[i])
        }
    }
    private fun setcurrentIndicator(index: Int){
        val childCount = introViewPagerLinearLayout.childCount
        for(i in 0 until childCount){
            val ImageView= introViewPagerLinearLayout[i] as ImageView
                if(i==index){
                    ImageView.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.indicator_active))
                }else{
                    ImageView.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.indicator_inactive))
                }
        }
    }
}