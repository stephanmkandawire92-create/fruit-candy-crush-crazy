package com.fruitcandycrushcarzy.APP.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onPlay: (Int) -> Unit
) {
    var screen by remember { mutableStateOf(HomeDestination.HOME) }
    var lives by remember { mutableIntStateOf(5) }
    var coins by remember { mutableIntStateOf(250) }
    var dailyClaimed by remember { mutableStateOf(false) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF6A1B9A), Color(0xFF1A237E), Color(0xFF0D47A1))
            )
        )
    ) {
        when (screen) {
            HomeDestination.HOME -> {
                HomeContent(
                    lives = lives,
                    coins = coins,
                    dailyClaimed = dailyClaimed,
                    onPlay = { onPlay(1) },
                    onLevels = { screen = HomeDestination.LEVELS },
                    onDaily = {
                        if (!dailyClaimed) {
                            dailyClaimed = true
                            coins += 100
                        }
                    },
                    onSettings = { screen = HomeDestination.SETTINGS }
                )
            }
            HomeDestination.LEVELS -> {
                LevelMap(
                    onBack = { screen = HomeDestination.HOME },
                    onLevel = { level ->
                        onPlay(level)
                    }
                )
            }
            HomeDestination.SETTINGS -> {
                SettingsInfo(onBack = { screen = HomeDestination.HOME })
            }
        }
    }
}

private enum class HomeDestination { HOME, LEVELS, SETTINGS }

@Composable
private fun HomeContent(
    lives: Int,
    coins: Int,
    dailyClaimed: Boolean,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onDaily: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        Text("🍓 🍊 🍇", fontSize = 42.sp)
        Text(
            "FRUIT CRUSH",
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 2.sp
        )
        Text(
            "CRUSH • COMBO • CONQUER",
            color = Color.White.copy(alpha = .8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatPill("❤️ $lives", Modifier.weight(1f))
            StatPill("🪙 $coins", Modifier.weight(1f))
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth().height(72.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD600),
                contentColor = Color(0xFF311B00)
            )
        ) {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text("PLAY NOW", fontSize = 22.sp, fontWeight = FontWeight.Black)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = onLevels,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.EmojiEvents, null)
            Spacer(Modifier.width(8.dp))
            Text("LEVEL MAP", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(14.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeAction(
                "DAILY
REWARD",
                Icons.Default.Redeem,
                !dailyClaimed,
                Modifier.weight(1f),
                onDaily
            )
            HomeAction(
                "SETTINGS",
                Icons.Default.Settings,
                false,
                Modifier.weight(1f),
                onSettings
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            "100+ LEVELS • OFFLINE PLAY • DAILY REWARDS",
            color = Color.White.copy(alpha = .65f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun StatPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = .12f)
    ) {
        Text(
            text,
            Modifier.padding(vertical = 12.dp),
            color = Color.White,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (active) Color(0xFFFFD600) else Color.White.copy(alpha = .3f)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(24.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun LevelMap(
    onBack: () -> Unit,
    onLevel: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← HOME", color = Color.White) }
            Text("LEVEL MAP", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        Text(
            "TROPICAL GARDEN • WORLD 1",
            color = Color.White.copy(alpha = .7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items((1..100).toList()) { level ->
                val unlocked = level <= 100
                LevelButton(level, unlocked) { onLevel(level) }
            }
        }
    }
}

@Composable
private fun LevelButton(level: Int, unlocked: Boolean, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) .94f else 1f, spring(), label = "level")
    Surface(
        modifier = Modifier.aspectRatio(1f).scale(scale),
        onClick = {
            if (unlocked) {
                pressed = true
                onClick()
            }
        },
        enabled = unlocked,
        shape = RoundedCornerShape(20.dp),
        color = if (unlocked) Color(0xFFFFD600) else Color.White.copy(alpha = .12f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (unlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐", fontSize = 20.sp)
                    Text("$level", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color(0xFF3E2723))
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = .55f))
                    Text("$level", color = Color.White.copy(alpha = .55f), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsInfo(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBack) { Text("← HOME", color = Color.White) }
        Spacer(Modifier.height(40.dp))
        Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(64.dp))
        Text("SETTINGS", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))
        Text(
            "Use the settings button inside a game to control music, sound effects and vibration.",
            color = Color.White.copy(alpha = .8f),
            textAlign = TextAlign.Center
        )
    }
}
