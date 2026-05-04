package com.fruitcandycrushcarzy.APP.game.logic

import com.fruitcandycrushcarzy.APP.game.model.Fruit
import com.fruitcandycrushcarzy.APP.game.model.Position
import com.fruitcandycrushcarzy.APP.game.model.SpecialType

object GameLogic {
    const val GRID_SIZE = 8

    fun createInitialGrid(): Array<Array<Fruit?>> {
        val grid = Array(GRID_SIZE) { arrayOfNulls<Fruit>(GRID_SIZE) }
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                var fruit: Fruit
                do {
                    fruit = Fruit.random()
                } while (wouldCreateMatch(grid, r, c, fruit))
                grid[r][c] = fruit
            }
        }
        return grid
    }

    private fun wouldCreateMatch(grid: Array<Array<Fruit?>>, row: Int, col: Int, fruit: Fruit): Boolean {
        // Check horizontal
        if (col >= 2 && grid[row][col - 1]?.type == fruit.type && grid[row][col - 2]?.type == fruit.type) return true
        // Check vertical
        if (row >= 2 && grid[row - 1][col]?.type == fruit.type && grid[row - 2][col]?.type == fruit.type) return true
        return false
    }

    fun findMatchGroups(grid: Array<Array<Fruit?>>): List<List<Position>> {
        val groups = mutableListOf<List<Position>>()

        // Horizontal matches
        for (r in 0 until GRID_SIZE) {
            var count = 1
            for (c in 1 until GRID_SIZE) {
                if (grid[r][c] != null && grid[r][c]?.type == grid[r][c - 1]?.type) {
                    count++
                } else {
                    if (count >= 3) {
                        groups.add((0 until count).map { Position(r, c - 1 - it) })
                    }
                    count = 1
                }
            }
            if (count >= 3) {
                groups.add((0 until count).map { Position(r, GRID_SIZE - 1 - it) })
            }
        }

        // Vertical matches
        for (c in 0 until GRID_SIZE) {
            var count = 1
            for (r in 1 until GRID_SIZE) {
                if (grid[r][c] != null && grid[r][c]?.type == grid[r - 1][c]?.type) {
                    count++
                } else {
                    if (count >= 3) {
                        groups.add((0 until count).map { Position(r - 1 - it, c) } )
                    }
                    count = 1
                }
            }
            if (count >= 3) {
                groups.add((0 until count).map { Position(GRID_SIZE - 1 - it, c) })
            }
        }

        return groups
    }

    fun getAffectedPositions(grid: Array<Array<Fruit?>>, matches: Set<Position>): Set<Position> {
        val affected = matches.toMutableSet()
        val toCheck = matches.toMutableList()
        val checked = mutableSetOf<Position>()

        while (toCheck.isNotEmpty()) {
            val pos = toCheck.removeAt(0)
            if (pos in checked) continue
            checked.add(pos)

            val fruit = grid[pos.row][pos.col] ?: continue
            when (fruit.special) {
                SpecialType.ROW_BLAST -> {
                    for (c in 0 until GRID_SIZE) {
                        val newPos = Position(pos.row, c)
                        if (affected.add(newPos)) toCheck.add(newPos)
                    }
                }
                SpecialType.COL_BLAST -> {
                    for (r in 0 until GRID_SIZE) {
                        val newPos = Position(r, pos.col)
                        if (affected.add(newPos)) toCheck.add(newPos)
                    }
                }
                SpecialType.BOMB -> {
                    for (r in (pos.row - 1)..(pos.row + 1)) {
                        for (c in (pos.col - 1)..(pos.col + 1)) {
                            if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                                val newPos = Position(r, c)
                                if (affected.add(newPos)) toCheck.add(newPos)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
        return affected
    }

    fun applyGravity(grid: Array<Array<Fruit?>>): List<Pair<Position, Position>> {
        val movements = mutableListOf<Pair<Position, Position>>()
        for (c in 0 until GRID_SIZE) {
            var emptyRow = GRID_SIZE - 1
            for (r in GRID_SIZE - 1 downTo 0) {
                if (grid[r][c] != null) {
                    if (r != emptyRow) {
                        movements.add(Position(r, c) to Position(emptyRow, c))
                        grid[emptyRow][c] = grid[r][c]
                        grid[r][c] = null
                    }
                    emptyRow--
                }
            }
        }
        return movements
    }

    fun refillGrid(grid: Array<Array<Fruit?>>): List<Pair<Fruit, Position>> {
        val newFruits = mutableListOf<Pair<Fruit, Position>>()
        for (c in 0 until GRID_SIZE) {
            for (r in 0 until GRID_SIZE) {
                if (grid[r][c] == null) {
                    val fruit = Fruit.random()
                    grid[r][c] = fruit
                    newFruits.add(fruit to Position(r, c))
                }
            }
        }
        return newFruits
    }

    fun isAdjacent(p1: Position, p2: Position): Boolean {
        return (Math.abs(p1.row - p2.row) == 1 && p1.col == p2.col) ||
               (Math.abs(p1.col - p2.col) == 1 && p1.row == p2.row)
    }

    fun hasAvailableMoves(grid: Array<Array<Fruit?>>): Boolean {
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                // Try swapping with right
                if (c < GRID_SIZE - 1) {
                    if (checkSwapMatch(grid, r, c, r, c + 1)) return true
                }
                // Try swapping with down
                if (r < GRID_SIZE - 1) {
                    if (checkSwapMatch(grid, r, c, r + 1, c)) return true
                }
            }
        }
        return false
    }

    private fun checkSwapMatch(grid: Array<Array<Fruit?>>, r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        val fruit1 = grid[r1][c1] ?: return false
        val fruit2 = grid[r2][c2] ?: return false
        
        // Horizontal check for r1, c1 after moving to r2, c2
        if (checkMatchAt(grid, r2, c2, fruit1, r1, c1)) return true
        // Vertical check for r1, c1 after moving to r2, c2
        if (checkMatchAt(grid, r2, c2, fruit1, r1, c1, horizontal = false)) return true
        
        // Horizontal check for r2, c2 after moving to r1, c1
        if (checkMatchAt(grid, r1, c1, fruit2, r2, c2)) return true
        // Vertical check for r2, c2 after moving to r1, c1
        if (checkMatchAt(grid, r1, c1, fruit2, r2, c2, horizontal = false)) return true
        
        return false
    }

    private fun checkMatchAt(grid: Array<Array<Fruit?>>, r: Int, c: Int, fruit: Fruit, skipR: Int, skipC: Int, horizontal: Boolean = true): Boolean {
        var count = 1
        if (horizontal) {
            // Check left
            var i = c - 1
            while (i >= 0) {
                if (i == skipC && r == skipR) break
                if (grid[r][i]?.type == fruit.type) count++ else break
                i--
            }
            // Check right
            i = c + 1
            while (i < GRID_SIZE) {
                if (i == skipC && r == skipR) break
                if (grid[r][i]?.type == fruit.type) count++ else break
                i++
            }
        } else {
            // Check up
            var i = r - 1
            while (i >= 0) {
                if (i == skipR && c == skipC) break
                if (grid[i][c]?.type == fruit.type) count++ else break
                i--
            }
            // Check down
            i = r + 1
            while (i < GRID_SIZE) {
                if (i == skipR && c == skipC) break
                if (grid[i][c]?.type == fruit.type) count++ else break
                i++
            }
        }
        return count >= 3
    }
}
