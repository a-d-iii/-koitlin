package com.example.basic.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

/** Simple pager replacing fragment_home.xml's ViewPager2 */
@Composable
fun HomePager(items: List<String>) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 2
        ) { page ->
            ItemCard(text = items[page])
        }
    }
}
