package com.fruitcandycrushcarzy.APP.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitcandycrushcarzy.APP.game.data.ScoreRepository
import com.fruitcandycrushcarzy.APP.game.logic.GameLogic
import com.fruitcandycrushcarzy.APP.game.model.Fruit
import com.fruitcandycrushcarzy.APP.game.model.FruitType
import com.fruitcandycrushcarzy.APP.game.model.Position
import com.fruitcandycrushcarzy.APP.game.model.SpecialType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GameEvent {
    MATCH, SWAP, GAME_OVER, LEVEL_UP, SPECIAL_EXPLOSION
}

enum class DragDirection {
    UP, DOWN, LEFT, RIGHT
}

data class GameState(
    val grid: Array<Array<Fruit?>> = Array(GameLogic.GRID_SIZE) { arrayOfNulls<Fruit>(GameLogic.GRID_SIZE) },
    val score: Int = 0,
    val highScore: Int = 0,
    val movesLeft: Int = 30,
    val selectedPosition: Position? = null,
    val isProcessing: Boolean = false,
    val level: Int = 1,
    val timeLeftSeconds: Int = 90,
    val targetScore: Int = 1000,
    val lastComboCount: Int = 0,
    val hasMoves: Boolean = true,
    val isLevelUp: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val showSettings: Boolean = false,
    val isStarting: Boolean = true,
    val showRateDialog: Boolean = false
)

class GameViewModel(private val scoreRepository: ScoreRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>()
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    init {
        val initialGrid = GameLogic.createInitialGrid()
        _uiState.update { it.copy(grid = initialGrid) }
        
        viewModelScope.launch {
            scoreRepository.highScoreFlow.collectLatest { high ->
                _uiState.update { it.copy(highScore = high) }
            }
        }

        viewModelScope.launch {
            scoreRepository.soundEnabledFlow.collectLatest { enabled ->
                _uiState.update { it.copy(isSoundEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            scoreRepository.musicEnabledFlow.collectLatest { enabled ->
                _uiState.update { it.copy(isMusicEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            scoreRepository.vibrationEnabledFlow.collectLatest { enabled ->
                _uiState.update { it.copy(isVibrationEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            scoreRepository.gamesPlayedFlow.collectLatest { games ->
                scoreRepository.hasRatedFlow.collectLatest { hasRated ->
                    if (!hasRated && games >= 3 && games % 5 == 0) {
                        _uiState.update { it.copy(showRateDialog = true) }
                    }
                }
            }
        }
        
        startGameSequence()
    }

    private fun startGameSequence() {
        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true) }
            delay(2500) // Match overlay duration
            _uiState.update { it.copy(isStarting = false) }
            startTimer()
        }
    }

    fun resetGame() {
        timerJob?.cancel()
        val initialGrid = GameLogic.createInitialGrid()
        _uiState.update { 
            GameState(
                grid = initialGrid,
                highScore = it.highScore,
                isSoundEnabled = it.isSoundEnabled,
                isMusicEnabled = it.isMusicEnabled,
                isVibrationEnabled = it.isVibrationEnabled
            )
        }
        viewModelScope.launch {
            scoreRepository.incrementGamesPlayed()
        }
        startGameSequence()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0 && _uiState.value.movesLeft > 0 && !_uiState.value.isLevelUp) {
                if (!_uiState.value.showSettings && !_uiState.value.isStarting) {
                    delay(1000)
                    _uiState.update { it.copy(timeLeftSeconds = (it.timeLeftSeconds - 1).coerceAtLeast(0)) }
                } else {
                    delay(100) // Poll more frequently when paused
                }
            }
        }
    }

    fun onCellClick(position: Position) {
        if (_uiState.value.isProcessing || _uiState.value.isStarting || _uiState.value.movesLeft <= 0 || _uiState.value.timeLeftSeconds <= 0 || _uiState.value.isLevelUp) return

        val selected = _uiState.value.selectedPosition
        if (selected == null) {
            _uiState.update { it.copy(selectedPosition = position) }
        } else {
            if (GameLogic.isAdjacent(selected, position)) {
                swapAndProcess(selected, position)
            } else {
                _uiState.update { it.copy(selectedPosition = position) }
            }
        }
    }

    private fun swapAndProcess(p1: Position, p2: Position) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, selectedPosition = null) }
            _events.emit(GameEvent.SWAP)

            // Perform swap
            val currentGrid = copyGrid(_uiState.value.grid)
            val temp = currentGrid[p1.row][p1.col]
            currentGrid[p1.row][p1.col] = currentGrid[p2.row][p2.col]
            currentGrid[p2.row][p2.col] = temp
            _uiState.update { it.copy(grid = currentGrid) }

            delay(300) // Swap animation delay

            val matches = GameLogic.findMatchGroups(currentGrid)
            if (matches.isEmpty()) {
                // Swap back if no matches
                val revertGrid = copyGrid(_uiState.value.grid)
                val tempBack = revertGrid[p1.row][p1.col]
                revertGrid[p1.row][p1.col] = revertGrid[p2.row][p2.col]
                revertGrid[p2.row][p2.col] = tempBack
                _uiState.update { it.copy(grid = revertGrid) }
            } else {
                // Valid move, decrement moves
                _uiState.update { it.copy(movesLeft = it.movesLeft - 1) }
                processMatches(currentGrid, triggeredBy = p2)
            }

            _uiState.update { it.copy(isProcessing = false) }
            checkMovesAvailable()
        }
    }

    private fun checkMovesAvailable() {
        val hasMoves = GameLogic.hasAvailableMoves(_uiState.value.grid)
        _uiState.update { it.copy(hasMoves = hasMoves) }
        
        if (!hasMoves && _uiState.value.movesLeft > 0 && !_uiState.value.isProcessing) {
            viewModelScope.launch {
                delay(1000)
                if (!GameLogic.hasAvailableMoves(_uiState.value.grid)) {
                    shuffleBoard(isAuto = true)
                }
            }
        }
    }

    private suspend fun processMatches(grid: Array<Array<Fruit?>>, triggeredBy: Position? = null) {
        var currentGrid = grid
        var combo = 0

        do {
            val groups = GameLogic.findMatchGroups(currentGrid)
            if (groups.isNotEmpty()) {
                combo++
                val matchPositions = groups.flatten().toSet()
                val affectedPositions = GameLogic.getAffectedPositions(currentGrid, matchPositions)
                
                if (affectedPositions.size > matchPositions.size) {
                    _events.emit(GameEvent.SPECIAL_EXPLOSION)
                }
                
                // Extra points for large matches and specials
                val basePoints = affectedPositions.size * 10
                val comboBonus = (combo - 1) * 20
                val specialBonus = if (affectedPositions.size > matchPositions.size) 100 else 0
                val points = (basePoints + comboBonus + specialBonus) * combo

                _events.emit(GameEvent.MATCH)

                // Logic to create special fruits
                val specialFruitsToCreate = mutableListOf<Triple<Position, FruitType, SpecialType>>()
                groups.forEach { group ->
                    if (group.size >= 4) {
                        val type = currentGrid[group[0].row][group[0].col]?.type ?: return@forEach
                        val specialType = when (group.size) {
                            4 -> if (group[0].row == group[1].row) SpecialType.COL_BLAST else SpecialType.ROW_BLAST
                            else -> SpecialType.BOMB
                        }
                        // Place special fruit at the position that triggered the match, or center of match
                        val pos = if (triggeredBy != null && triggeredBy in group) triggeredBy else group[0]
                        specialFruitsToCreate.add(Triple(pos, type, specialType))
                    }
                }

                // Clear matches
                affectedPositions.forEach { pos ->
                    currentGrid[pos.row][pos.col] = null
                }
                
                // Add special fruits
                specialFruitsToCreate.forEach { (pos, type, special) ->
                    currentGrid[pos.row][pos.col] = Fruit(type, special)
                }
                
                val currentScore = _uiState.value.score
                val newScore = currentScore + points
                _uiState.update { it.copy(
                    grid = copyGrid(currentGrid), 
                    score = newScore,
                    lastComboCount = combo
                ) }
                
                if (newScore > _uiState.value.highScore) {
                    scoreRepository.updateHighScore(newScore)
                }

                delay(400) // Pop animation delay

                // Apply gravity
                GameLogic.applyGravity(currentGrid)
                _uiState.update { it.copy(grid = copyGrid(currentGrid)) }
                delay(300) // Falling animation delay

                // Refill
                GameLogic.refillGrid(currentGrid)
                _uiState.update { it.copy(grid = copyGrid(currentGrid)) }
                delay(300) // New fruits animation delay
            }
        } while (GameLogic.findMatchGroups(currentGrid).isNotEmpty())

        // Check for level up
        if (_uiState.value.score >= _uiState.value.targetScore) {
            levelUp()
        }
    }

    private fun levelUp() {
        viewModelScope.launch {
            _events.emit(GameEvent.LEVEL_UP)
            _uiState.update { it.copy(isLevelUp = true) }
            delay(2000)
            _uiState.update { 
                it.copy(
                    level = it.level + 1,
                    targetScore = it.targetScore + (it.level + 1) * 1000,
                    movesLeft = it.movesLeft + 10,
                    timeLeftSeconds = it.timeLeftSeconds + 60,
                    isLevelUp = false
                )
            }
            startTimer()
        }
    }

    fun shuffleBoard(isAuto: Boolean = false) {
        if (_uiState.value.isProcessing || _uiState.value.isLevelUp) return
        
        val cost = if (isAuto) 0 else (if (_uiState.value.hasMoves) 2 else 0)
        if (_uiState.value.movesLeft < cost) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, movesLeft = it.movesLeft - cost) }
            
            val newGrid = GameLogic.createInitialGrid()
            _uiState.update { it.copy(grid = newGrid, hasMoves = true) }
            
            delay(500)
            _uiState.update { it.copy(isProcessing = false) }
            checkMovesAvailable()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            scoreRepository.toggleSound(!_uiState.value.isSoundEnabled)
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            scoreRepository.toggleMusic(!_uiState.value.isMusicEnabled)
        }
    }

    fun toggleVibration() {
        viewModelScope.launch {
            scoreRepository.toggleVibration(!_uiState.value.isVibrationEnabled)
        }
    }

    fun toggleSettings() {
        _uiState.update { it.copy(showSettings = !it.showSettings) }
    }

    fun onRateApp() {
        viewModelScope.launch {
            scoreRepository.setHasRated(true)
            _uiState.update { it.copy(showRateDialog = false) }
        }
    }

    fun onDismissRateDialog() {
        _uiState.update { it.copy(showRateDialog = false) }
    }

    fun onSwipe(position: Position, direction: DragDirection) {
        if (_uiState.value.isProcessing || _uiState.value.isStarting || _uiState.value.movesLeft <= 0 || _uiState.value.timeLeftSeconds <= 0 || _uiState.value.isLevelUp) return
        
        val targetPos = when (direction) {
            DragDirection.UP -> Position(position.row - 1, position.col)
            DragDirection.DOWN -> Position(position.row + 1, position.col)
            DragDirection.LEFT -> Position(position.row, position.col - 1)
            DragDirection.RIGHT -> Position(position.row, position.col + 1)
        }
        
        if (targetPos.row in 0 until GameLogic.GRID_SIZE && targetPos.col in 0 until GameLogic.GRID_SIZE) {
            swapAndProcess(position, targetPos)
        }
    }

    private fun copyGrid(original: Array<Array<Fruit?>>): Array<Array<Fruit?>> {
        return Array(GameLogic.GRID_SIZE) { r ->
            Array(GameLogic.GRID_SIZE) { c ->
                original[r][c]
            }
        }
    }
}
