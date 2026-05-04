# Project Plan

Fruit Crush game called FRUIT CANDY CRUSH CARZY. 
Features:
- 8x8 grid of fruit emojis (🍎🍊🍇🍓🍌🥝🍑🍒)
- Click-to-swap mechanics
- Match detection (3+ in a row/column)
- Gravity and cascading (fruits fall down, new ones appear)
- Score tracking and levels
- Timer and move limit (30 moves)
- Shuffle button (costs 2 moves)
- Animated effects (pop effects, falling fruits, combo text)
- Material Design 3, vibrant color scheme, adaptive icon, edge-to-edge display.

## Project Brief

# Project Brief: FRUIT CANDY CRUSH CARZY

A vibrant, energetic match-3 puzzle game featuring colorful fruit emojis, engaging animations, and Material Design 3 principles.

### Features

*   **8x8 Match-3 Grid Engine:** Interactive grid allowing players to swap fruit emojis (🍎🍊🍇🍓🍌🥝🍑🍒) to create matches of three or more in a row or column.
*   **Dynamic Cascading & Gravity:** Automatic detection of matches followed by fruits "popping" and new fruits falling from the top to fill the gaps.
*   **Move-Based Progression:** A strategic scoring system with a strict 30-move limit and level-based goals.
*   **Board Shuffle Utility:** A tactical shuffle button that reshuffles the board when no moves are available, costing the player 2 moves.
*   **Vibrant M3 Interface:** Full edge-to-edge display using Material Design 3 with energetic color schemes and pop animations for combos.

### High-Level Technical Stack
*   **Kotlin:** Primary programming language for game logic and state handling.
*   **Jetpack Compose:** Declarative UI framework used for the game grid, animations, and Material 3 components.
*   **Kotlin Coroutines & Flow:** Manages the game loop, timed events (cascading delays), and reactive state updates.
*   **ViewModel:** Maintains the game state (grid layout, score, move count) through configuration changes.
*   **KSP (Kotlin Symbol Processing):** Used for efficient code generation for any internal dependency injections or library requirements.

## Implementation Steps

### Task_1_Engine: Develop the core game engine and state management, including the 8x8 grid model, fruit emoji constants, match-3 detection logic, and the cascading/gravity algorithm within a ViewModel.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - 8x8 grid correctly initialized
  - Match-3 (horizontal/vertical) detection logic works
  - Gravity and cascading logic correctly refills the board in the ViewModel
- **StartTime:** 2026-05-03 20:17:02 SAST

### Task_2_UI_Implementation: Create the user interface using Jetpack Compose, featuring the 8x8 game grid, a HUD for score and moves, and implement Material Design 3 with edge-to-edge support.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Game board displays fruit emojis correctly
  - HUD shows real-time score and moves
  - M3 vibrant color scheme and edge-to-edge display implemented

### Task_3_Gameplay_Animations: Implement interactive swapping, game mechanics like the shuffle utility (costing 2 moves), move limit logic, and visual animations for fruit popping and cascading.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Click-to-swap interactions are responsive
  - Shuffle button works and deducts 2 moves
  - Animations for matching and falling fruits are smooth
  - Game ends when moves reach zero

### Task_4_Finalization_Verification: Create an adaptive app icon, refine the vibrant M3 theme, and conduct a final run to verify stability and requirement alignment.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Adaptive app icon matches the game theme
  - Build passes successfully
  - App does not crash during gameplay
  - Critic agent verifies application stability (no crashes), confirms alignment with user requirements, and reports critical UI issues

