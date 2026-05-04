package com.fruitcandycrushcarzy.APP.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.border
import com.fruitcandycrushcarzy.APP.game.model.Fruit
import com.fruitcandycrushcarzy.APP.game.model.SpecialType
import com.fruitcandycrushcarzy.APP.game.viewmodel.DragDirection
import kotlin.math.abs

@Composable
fun FruitCell(
    fruit: Fruit?,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSwipe: (DragDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragAccumulated by remember { mutableStateOf(Offset.Zero) }
    var swiped by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "selectionScale"
    )

    val specialBorder = when (fruit?.special) {
        SpecialType.ROW_BLAST -> Color.Cyan
        SpecialType.COL_BLAST -> Color.Magenta
        SpecialType.BOMB -> Color.Yellow
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = if (isSelected) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .then(
                if (specialBorder != Color.Transparent) {
                    Modifier.border(2.dp, specialBorder, RoundedCornerShape(12.dp))
                } else Modifier
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragAccumulated = Offset.Zero
                        swiped = false
                    },
                    onDrag = { _, dragAmount ->
                        if (!swiped) {
                            dragAccumulated += dragAmount
                            val threshold = 40f // reduced threshold for better responsiveness
                            if (abs(dragAccumulated.x) > threshold || abs(dragAccumulated.y) > threshold) {
                                swiped = true
                                if (abs(dragAccumulated.x) > abs(dragAccumulated.y)) {
                                    if (dragAccumulated.x > 0) onSwipe(DragDirection.RIGHT) else onSwipe(DragDirection.LEFT)
                                } else {
                                    if (dragAccumulated.y > 0) onSwipe(DragDirection.DOWN) else onSwipe(DragDirection.UP)
                                }
                            }
                        }
                    }
                )
            }
            .clickable(onClick = onClick)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = fruit != null,
            enter = scaleIn(animationSpec = tween(300)),
            exit = scaleOut(animationSpec = tween(300))
        ) {
            if (fruit != null) {
                Text(
                    text = fruit.emoji,
                    fontSize = 32.sp
                )
            }
        }
    }
}
