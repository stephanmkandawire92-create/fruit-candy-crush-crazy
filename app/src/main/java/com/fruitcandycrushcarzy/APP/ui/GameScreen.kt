package com.fruitcandycrushcarzy.APP.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fruitcandycrushcarzy.APP.R
import com.fruitcandycrushcarzy.APP.game.model.Position
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameViewModel
import com.fruitcandycrushcarzy.APP.ui.components.FruitCell
import kotlinx.coroutines.delay

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Dynamic theme colors that change every level to keep the game fresh
    val themeColors = remember(uiState.level) {
        when (uiState.level % 4) {
            1 -> Triple(listOf(Color(0xFF1A237E), Color(0xFF4A148C)), Color.Yellow, Color(0xFF311B92))
            2 -> Triple(listOf(Color(0xFF004D40), Color(0xFF00BCD4)), Color(0xFFFFD600), Color(0xFF006064))
            3 -> Triple(listOf(Color(0xFF3E2723), Color(0xFFBF360C)), Color(0xFFFFAB40), Color(0xFF5D4037))
            else -> Triple(listOf(Color(0xFF263238), Color(0xFF37474F)), Color(0xFF00E676), Color(0xFF212121))
        }
    }

    val bgColors = themeColors.first
    val accentColor = themeColors.second
    val cardBg = themeColors.third

    val animatedBgStart by animateColorAsState(targetValue = bgColors[0], animationSpec = tween(1500))
    val animatedBgEnd by animateColorAsState(targetValue = bgColors[1], animationSpec = tween(1500))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedBgStart, animatedBgEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Premium Header Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LEVEL ${uiState.level}",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = " FRUIT CRUSH ",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                
                IconButton(
                    onClick = { viewModel.toggleSettings() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Professional Progress and Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SCORE", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = uiState.score.toString(),
                                color = accentColor,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BEST", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = uiState.highScore.toString(),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TARGET", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = uiState.targetScore.toString(),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val progress by animateFloatAsState(
                        targetValue = (uiState.score.toFloat() / uiState.targetScore.toFloat()).coerceIn(0f, 1f),
                        animationSpec = tween(1200, easing = FastOutSlowInEasing)
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth().height(14.dp)) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(7.dp)))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(accentColor, Color.White)),
                                    shape = RoundedCornerShape(7.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "MOVES LEFT", 
                    value = uiState.movesLeft.toString(), 
                    modifier = Modifier.weight(1f),
                    color = if (uiState.movesLeft <= 5) Color(0xFFFF5252) else accentColor
                )
                StatCard(
                    label = "TIME", 
                    value = "${uiState.timeLeftSeconds}s", 
                    modifier = Modifier.weight(1f),
                    color = if (uiState.timeLeftSeconds <= 15) Color(0xFFFF5252) else Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Polished Game Board
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.Black.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Column {
                        for (r in 0 until 8) {
                            Row(modifier = Modifier.weight(1f)) {
                                for (c in 0 until 8) {
                                    val pos = Position(r, c)
                                    val scale by animateFloatAsState(
                                        targetValue = if (uiState.isStarting) 0f else 1f,
                                        animationSpec = spring(dampingRatio = 0.65f, stiffness = 300f)
                                    )

                                    FruitCell(
                                        fruit = uiState.grid[r][c],
                                        isSelected = uiState.selectedPosition == pos,
                                        onClick = { viewModel.onCellClick(pos) },
                                        onSwipe = { direction -> viewModel.onSwipe(pos, direction) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .scale(scale)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Combo Visual Feedback
                    Column {
                        AnimatedVisibility(
                            visible = uiState.lastComboCount > 1 && uiState.isProcessing,
                            enter = scaleIn(spring(0.4f)) + fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = "COMBO X${uiState.lastComboCount}!",
                                color = accentColor,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // No Moves Feedback
                    Column {
                        AnimatedVisibility(
                            visible = !uiState.hasMoves && !uiState.isProcessing && !uiState.isStarting,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Text(
                                text = "NO MOVES!\nSHUFFLING...",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                                    .padding(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Premium Shuffle Button
            val shuffleCost = if (uiState.hasMoves) 2 else 0
            val shuffleText = if (uiState.hasMoves) "SHUFFLE BOARD (2 MOVES)" else "SHUFFLE BOARD (FREE)"
            Button(
                onClick = { viewModel.shuffleBoard() },
                enabled = uiState.movesLeft >= shuffleCost && !uiState.isProcessing && !uiState.isStarting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                shape = RoundedCornerShape(22.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
            ) {
                Text(shuffleText, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Overlay Management
        if (uiState.isStarting) {
            StartAnimationOverlay()
        }

        if ((uiState.movesLeft <= 0 || uiState.timeLeftSeconds <= 0) && !uiState.isProcessing && !uiState.isLevelUp) {
            GameOverOverlay(
                score = uiState.score, 
                highScore = uiState.highScore,
                onRestart = { viewModel.resetGame() }
            )
        }

        if (uiState.isLevelUp) {
            LevelUpOverlay(level = uiState.level + 1)
        }

        if (uiState.showSettings) {
            SettingsOverlay(
                isSoundEnabled = uiState.isSoundEnabled,
                isMusicEnabled = uiState.isMusicEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onToggleMusic = { viewModel.toggleMusic() },
                isVibrationEnabled = uiState.isVibrationEnabled,
                onToggleVibration = { viewModel.toggleVibration() },
                onClose = { viewModel.toggleSettings() }
            )
        }

        if (uiState.showRateDialog) {
            RateAppOverlay(
                onRate = { viewModel.onRateApp() },
                onDismiss = { viewModel.onDismissRateDialog() }
            )
        }
    }
}

@Composable
fun RateAppOverlay(onRate: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ENJOYING THE GAME?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "RATE US!",
                    color = Color(0xFF4A148C),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your feedback helps us make Fruit Crush even better for everyone!",
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onRate,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("RATE NOW", fontWeight = FontWeight.Black)
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("MAYBE LATER", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, color: Color = Color.White) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.5f))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun StartAnimationOverlay() {
    var phase by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        delay(800)
        phase = 1 // READY?
        delay(1200)
        phase = 2 // GO!
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.fruit_crush_logo),
                contentDescription = null,
                modifier = Modifier.size(240.dp).padding(bottom = 40.dp)
            )
            
            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    (scaleIn(animationSpec = spring(0.5f)) + fadeIn()).togetherWith(scaleOut() + fadeOut())
                },
                label = "StartPhase"
            ) { currentPhase ->
                val text = when(currentPhase) {
                    1 -> "READY?"
                    2 -> "GO!"
                    else -> ""
                }
                Text(
                    text = text,
                    color = Color.Yellow,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun LevelUpOverlay(level: Int) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "FANTASTIC!",
                color = Color.Cyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "LEVEL UP",
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Black
            )
            Surface(
                color = Color.Yellow,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = " NEXT: LEVEL $level ",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GameOverOverlay(score: Int, highScore: Int, onRestart: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(40.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GAME OVER", color = Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    score.toString(),
                    color = Color(0xFF4A148C),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                )
                Text("TOTAL SCORE", color = Color.Black.copy(alpha = 0.3f), fontSize = 14.sp, fontWeight = FontWeight.Black)
                
                if (score >= highScore && score > 0) {
                    Text(
                        "NEW BEST!",
                        color = Color(0xFFFFD600),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("BEST SCORE", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(highScore.toString(), color = Color.Black, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("PLAY AGAIN", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsOverlay(
    isSoundEnabled: Boolean,
    isMusicEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    isVibrationEnabled: Boolean,
    onToggleVibration: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)).clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f).clickable(enabled = false) { },
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SETTINGS", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
                Spacer(modifier = Modifier.height(24.dp))
                
                SettingsRow("Background Music", isMusicEnabled, onToggleMusic)
                SettingsRow("Sound Effects", isSoundEnabled, onToggleSound)
                SettingsRow("Haptic Feedback", isVibrationEnabled, onToggleVibration)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TextButton(onClick = onClose) {
                    Text("CLOSE", fontWeight = FontWeight.Black, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SettingsRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked, 
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4A148C))
        )
    }
}
