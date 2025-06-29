package com.example.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardCarousel(
    cards: List<ClassInfo>,
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit,
    onIndexChange: ((Int) -> Unit)? = null,
    locationName: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val scope = rememberCoroutineScope()
    var dragAmount by remember { mutableStateOf(0f) }

    // Calculate vertical offset for the number row so it sits just under the cards
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp
    val headerHeight = screenHeight * 0.1f
    val carouselHeight = screenHeight - headerHeight
    val cardHeight = screenHeight * 0.7f
    // Position the number row just below the cards when they are centered.
    val numberTop = (carouselHeight - cardHeight) / 2f + cardHeight + 4.dp

    LaunchedEffect(pagerState.currentPage) {
        onIndexChange?.invoke(pagerState.currentPage)
    }

    val dragModifier = if (pagerState.currentPage > 0) {
        Modifier.pointerInput(pagerState.currentPage) {
            detectDragGestures(
                onDrag = { _, drag ->
                    dragAmount += drag.y
                },
                onDragEnd = {
                    if (dragAmount > 40f) onSwipeDown() else if (dragAmount < -40f) onSwipeUp()
                    dragAmount = 0f
                }
            )
        }
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
            .then(dragModifier)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { clip = false },
            beyondBoundsPageCount = 2
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (page == 0) {
                    SummaryCard()
                } else {
                    ClassCard(
                        info = cards[page],
                        index = page - 1,
                        daySchedule = cards.drop(1),
                        locationName = locationName
                    )
                }
            }
        }

        if (pagerState.currentPage > 0) {
            NumberRowAnimated(
                count = cards.size - 1,
                currentPage = pagerState.currentPage - 1,
                pageOffset = pagerState.currentPageOffsetFraction,
                onTap = { idx -> scope.launch { pagerState.animateScrollToPage(idx + 1) } },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = numberTop)
            )

            Text(
                text = "Swipe left/right to see all classes",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                color = Color(0xFF555555),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun NumberRowAnimated(
    count: Int,
    currentPage: Int,
    pageOffset: Float,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = 32.dp
    val circleSize = 16.dp
    Row(
        modifier = modifier
            .height(circleSize * 1.5f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { idx ->
            val pagePos = currentPage + pageOffset
            val distance = abs(pagePos - idx)
            val scaleNum = 1f + 0.3f * (1f - distance.coerceIn(0f, 1f))
            val scaleCircle = 1f + 0.5f * (1f - distance.coerceIn(0f, 1f))
            val animNum by animateFloatAsState(targetValue = scaleNum)
            val animCircle by animateFloatAsState(targetValue = scaleCircle)
            Box(
                modifier = Modifier
                    .width(spacing)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTap(idx) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .graphicsLayer(scaleX = animCircle, scaleY = animCircle)
                        .background(Color(0xFF333333), CircleShape)
                )
                Text(
                    text = "${idx + 1}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.graphicsLayer(scaleX = animNum, scaleY = animNum)
                )
            }
        }
    }
}
