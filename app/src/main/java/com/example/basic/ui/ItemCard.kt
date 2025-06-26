package com.example.basic.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Replacement for item_card.xml using Compose ConstraintLayout to avoid nested layouts */
@Composable
fun ItemCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        androidx.constraintlayout.compose.ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (label) = createRefs()
            Text(
                text = text,
                modifier = Modifier
                    .constrainAs(label) {
                        centerTo(parent)
                    }
            )
        }
    }
}
