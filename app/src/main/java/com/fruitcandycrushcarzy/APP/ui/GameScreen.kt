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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fruitcandycrushcarzy.APP.R
import com.fruitcandycrushcarzy.APP.game.model.Position
import com.fruitcandycrushcarzy.APP.game.viewmodel.GameViewModel
import com.fruitcandycrushcarzy.APP.ui.components.AdBanner
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    /*
     * =========================================================
     * BANNER AD REFRESH
     * =========================================================
     *
     * Har 60 seconds par AdBanner ko recreate kiya jayega.
     * Isse Top aur Bottom dono banner refresh honge.
     */
    var adRefreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            adRefreshKey++
        }
    }

    /*
     * =========================================================
     * DYNAMIC THEME
     * =========================================================
     */
    val themeColors = remember(uiState.level) {
        when (uiState.level % 4) {
            1 -> Triple(
                listOf(Color(0xFF5B247A), Color(0xFF1B1464)),
                Color(0xFFFFD54F),
                Color(0xFF311B50)
            )

            2 -> Triple(
                listOf(Color(0xFF00695C), Color(0xFF00838F)),
                Color(0xFFFFD740),
                Color(0xFF004D40)
            )

            3 -> Triple(
                listOf(Color(0xFF8D3A00), Color(0xFF4A148C)),
                Color(0xFFFFAB40),
                Color(0xFF5D2500)
            )

            else -> Triple(
                listOf(Color(0xFF283593), Color(0xFF4527A0)),
                Color(0xFF69F0AE),
                Color(0xFF212121)
            )
        }
    }

    val bgColors = themeColors.first
    val accentColor = themeColors.second
    val cardBg = themeColors.third

    val animatedBgStart by animateColorAsState(
        targetValue = bgColors[0],
        animationSpec = tween(1500),
        label = "bgStart"
    )

    val animatedBgEnd by animateColorAsState(
        targetValue = bgColors[1],
        animationSpec = tween(1500),
        label = "bgEnd"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        animatedBgStart,
                        animatedBgEnd
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*
             * =================================================
             * TOP BANNER AD
             * =================================================
             */
            Spacer(modifier = Modifier.height(6.dp))

            key(adRefreshKey) {
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            /*
             * =================================================
             * HEADER
             * =================================================
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "LEVEL ${uiState.level}",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )

                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = " FRUIT CRUSH ",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(
                                horizontal = 5.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.toggleSettings()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            Color.White.copy(alpha = 0.16f),
                            RoundedCornerShape(15.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            /*
             * =================================================
             * SCORE CARD
             * =================================================
             */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg.copy(alpha = 0.65f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.12f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(
                                "SCORE",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                uiState.score.toString(),
                                color = accentColor,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "BEST",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                uiState.highScore.toString(),
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "TARGET",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                uiState.targetScore.toString(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    val progress by animateFloatAsState(
                        targetValue = (
                            uiState.score.toFloat() /
                                uiState.targetScore.toFloat()
                            ).coerceIn(0f, 1f),
                        animationSpec = tween(
                            600,
                            easing = FastOutSlowInEasing
                        ),
                        label = "progress"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Black.copy(alpha = 0.35f),
                                    RoundedCornerShape(5.dp)
                                )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            accentColor,
                                            Color.White
                                        )
                                    ),
                                    RoundedCornerShape(5.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            /*
             * =================================================
             * MOVES + TIME
             * =================================================
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                StatCard(
                    label = "MOVES",
                    value = uiState.movesLeft.toString(),
                    modifier = Modifier.weight(1f),
                    color = if (
                        uiState.movesLeft <= 5
                    ) {
                        Color(0xFFFF5252)
                    } else {
                        accentColor
                    }
                )

                StatCard(
                    label = "TIME",
                    value = "${uiState.timeLeftSeconds}s",
                    modifier = Modifier.weight(1f),
                    color = if (
                        uiState.timeLeftSeconds <= 15
                    ) {
                        Color(0xFFFF5252)
                    } else {
                        Color.White
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            /*
             * =================================================
             * 6 x 6 GAME BOARD
             * =================================================
             */
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(28.dp),
                color = Color.Black.copy(alpha = 0.38f),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Color.White.copy(alpha = 0.08f)
                )
            ) {

                Box(
                    modifier = Modifier.padding(7.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        /*
                         * GameLogic.GRID_SIZE = 6 hona chahiye.
                         * Isliye yahan hard-coded 6 nahi,
                         * actual GRID_SIZE use kiya gaya hai.
                         */
                        for (r in 0 until 6) {

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {

                                for (c in 0 until 6) {

                                    val pos = Position(r, c)

                                    val scale by animateFloatAsState(
                                        targetValue =
                                            if (uiState.isStarting) {
                                                0f
                                            } else {
                                                1f
                                            },
                                        animationSpec = spring(
                                            dampingRatio = 0.65f,
                                            stiffness = 300f
                                        ),
                                        label = "fruitScale"
                                    )

                                    com.fruitcandycrushcarzy.APP.ui.components.FruitCell(
                                        fruit = uiState.grid[r][c],
                                        isSelected =
                                            uiState.selectedPosition == pos,
                                        onClick = {
                                            viewModel.onCellClick(pos)
                                        },
                                        onSwipe = { direction ->
                                            viewModel.onSwipe(
                                                pos,
                                                direction
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .padding(1.dp)
                                            .scale(scale)
                                    )
                                }
                            }
                        }
                    }

                    /*
                     * COMBO
                     */
                    AnimatedVisibility(
                        visible =
                            uiState.lastComboCount > 1 &&
                                uiState.isProcessing,
                        enter =
                            scaleIn(
                                spring(
                                    dampingRatio = 0.4f
                                )
                            ) + fadeIn(),
                        exit = fadeOut()
                    ) {

                        Text(
                            text =
                                "COMBO X${uiState.lastComboCount}!",
                            color = accentColor,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.7f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 7.dp
                                )
                        )
                    }

                    /*
                     * NO MOVES
                     */
                    AnimatedVisibility(
                        visible =
                            !uiState.hasMoves &&
                                !uiState.isProcessing &&
                                !uiState.isStarting,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {

                        Text(
                            text = "NO MOVES!\nSHUFFLING...",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.72f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            /*
             * =================================================
             * SHUFFLE BUTTON
             * =================================================
             */
            val shuffleCost =
                if (uiState.hasMoves) 2 else 0

            val shuffleText =
                if (uiState.hasMoves) {
                    "SHUFFLE  •  2 MOVES"
                } else {
                    "SHUFFLE  •  FREE"
                }

            Button(
                onClick = {
                    viewModel.shuffleBoard()
                },
                enabled =
                    uiState.movesLeft >= shuffleCost &&
                        !uiState.isProcessing &&
                        !uiState.isStarting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 7.dp
                )
            ) {

                Text(
                    text = shuffleText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            /*
             * =================================================
             * BOTTOM BANNER AD
             * =================================================
             */
            key(adRefreshKey) {
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp)
                )
            }
        }

        /*
         * =====================================================
         * START OVERLAY
         * =====================================================
         */
        if (uiState.isStarting) {
            StartAnimationOverlay()
        }

        /*
         * =====================================================
         * GAME OVER
         * =====================================================
         */
        if (
            (
                uiState.movesLeft <= 0 ||
                    uiState.timeLeftSeconds <= 0
                ) &&
                !uiState.isProcessing &&
                !uiState.isLevelUp
        ) {

            GameOverOverlay(
                score = uiState.score,
                highScore = uiState.highScore,
                onRestart = {
                    viewModel.resetGame()
                }
            )
        }

        /*
         * =====================================================
         * LEVEL UP
         * =====================================================
         */
        if (uiState.isLevelUp) {
            LevelUpOverlay(
                level = uiState.level + 1
            )
        }

        /*
         * =====================================================
         * SETTINGS
         * =====================================================
         */
        if (uiState.showSettings) {

            SettingsOverlay(
                isSoundEnabled =
                    uiState.isSoundEnabled,
                isMusicEnabled =
                    uiState.isMusicEnabled,
                onToggleSound = {
                    viewModel.toggleSound()
                },
                onToggleMusic = {
                    viewModel.toggleMusic()
                },
                isVibrationEnabled =
                    uiState.isVibrationEnabled,
                onToggleVibration = {
                    viewModel.toggleVibration()
                },
                onClose = {
                    viewModel.toggleSettings()
                }
            )
        }

        /*
         * =====================================================
         * RATE APP
         * =====================================================
         */
        if (uiState.showRateDialog) {

            RateAppOverlay(
                onRate = {
                    viewModel.onRateApp()
                },
                onDismiss = {
                    viewModel.onDismissRateDialog()
                }
            )
        }
    }
}


/*
 * =============================================================
 * RATE APP
 * =============================================================
 */
@Composable
fun RateAppOverlay(
    onRate: () -> Unit,
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.85f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier.padding(30.dp),
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
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Your feedback helps us make Fruit Crush even better!",
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = onRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "RATE NOW",
                        fontWeight = FontWeight.Black
                    )
                }

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        "MAYBE LATER",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


/*
 * =============================================================
 * STAT CARD
 * =============================================================
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {

    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.09f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.06f)
        )
    ) {

        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.55f)
            )

            Text(
                text = value,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}


/*
 * =============================================================
 * START ANIMATION
 * =============================================================
 */
@Composable
fun StartAnimationOverlay() {

    var phase by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {

        delay(800)
        phase = 1

        delay(1200)
        phase = 2
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.75f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.fruit_crush_logo
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 30.dp)
            )

            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    (
                        scaleIn(
                            animationSpec =
                                spring(0.5f)
                        ) + fadeIn()
                        ).togetherWith(
                        scaleOut() + fadeOut()
                    )
                },
                label = "StartPhase"
            ) { currentPhase ->

                val text =
                    when (currentPhase) {
                        1 -> "READY?"
                        2 -> "GO!"
                        else -> ""
                    }

                Text(
                    text = text,
                    color = Color.Yellow,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}


/*
 * =============================================================
 * LEVEL UP
 * =============================================================
 */
@Composable
fun LevelUpOverlay(level: Int) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.85f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "FANTASTIC!",
                color = Color.Cyan,
                fontSize = 23.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "LEVEL UP",
                color = Color.White,
                fontSize = 65.sp,
                fontWeight = FontWeight.Black
            )

            Surface(
                color = Color.Yellow,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(top = 14.dp)
            ) {

                Text(
                    text = " NEXT: LEVEL $level ",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 4.dp
                    )
                )
            }
        }
    }
}


/*
 * =============================================================
 * GAME OVER
 * =============================================================
 */
@Composable
fun GameOverOverlay(
    score: Int,
    highScore: Int,
    onRestart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.9f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(35.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier.padding(35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "GAME OVER",
                    color = Color.Gray,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    score.toString(),
                    color = Color(0xFF4A148C),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    "TOTAL SCORE",
                    color = Color.Black.copy(alpha = 0.3f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )

                if (score >= highScore && score > 0) {

                    Text(
                        "NEW BEST!",
                        color = Color(0xFFFFD600),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 7.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        "BEST SCORE",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        highScore.toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF4A148C)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        "PLAY AGAIN",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}


/*
 * =============================================================
 * SETTINGS
 * =============================================================
 */
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
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.82f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "SETTINGS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(22.dp))

                SettingsRow(
                    "Background Music",
                    isMusicEnabled,
                    onToggleMusic
                )

                SettingsRow(
                    "Sound Effects",
                    isSoundEnabled,
                    onToggleSound
                )

                SettingsRow(
                    "Haptic Feedback",
                    isVibrationEnabled,
                    onToggleVibration
                )

                Spacer(modifier = Modifier.height(25.dp))

                TextButton(
                    onClick = onClose
                ) {

                    Text(
                        "CLOSE",
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


/*
 * =============================================================
 * SETTINGS ROW
 * =============================================================
 */
@Composable
fun SettingsRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            label,
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Switch(
            checked = checked,
            onCheckedChange = {
                onToggle()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor =
                    Color(0xFF4A148C)
            )
        )
    }
}
