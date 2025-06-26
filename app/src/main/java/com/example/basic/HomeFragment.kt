package com.example.basic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        val cards = listOf("Card 1", "Card 2", "Card 3", "Card 4", "Card 5")
        viewPager.adapter = CardAdapter(cards)

        viewPager.offscreenPageLimit = 3

        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.setItemViewCacheSize(10)
        recyclerView.layoutManager?.isItemPrefetchEnabled = true

        val transformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(16))
            addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
        }
        viewPager.setPageTransformer(transformer)
    }
}
