package com.example.googlemapsexample.Models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googlemapsexample.R
import kotlinx.android.synthetic.main.slide_item_container.view.*

class IntroScreenAdapter(private val introSlides: List<IntroScreen>) :
    RecyclerView.Adapter<IntroScreenAdapter.IntroSlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container,parent,false))
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {

        return holder.bind(introSlides[position])
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    inner class IntroSlideViewHolder(view:View): RecyclerView.ViewHolder(view){
        private val textTitle=view.findViewById<TextView>(R.id.textTitle)
        private val textDescription=view.findViewById<TextView>(R.id.textDescription)
        private val imageIcon=view.findViewById<ImageView>(R.id.imageSlideIcon)

            fun bind(introScreen: IntroScreen){
                textTitle.text=introScreen.title
                textDescription.text=introScreen.description
                imageIcon.setImageResource(introScreen.icon)
            }


    }
}