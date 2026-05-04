package com.fruitcandycrushcarzy.APP.game.model

enum class FruitType {
    APPLE, ORANGE, GRAPE, STRAWBERRY, BANANA, KIWI, PEACH, CHERRY
}

enum class SpecialType {
    NONE, ROW_BLAST, COL_BLAST, BOMB
}

data class Fruit(
    val type: FruitType,
    val special: SpecialType = SpecialType.NONE
) {
    val emoji: String
        get() = when (type) {
            FruitType.APPLE -> "🍎"
            FruitType.ORANGE -> "🍊"
            FruitType.GRAPE -> "🍇"
            FruitType.STRAWBERRY -> "🍓"
            FruitType.BANANA -> "🍌"
            FruitType.KIWI -> "🥝"
            FruitType.PEACH -> "🍑"
            FruitType.CHERRY -> "🍒"
        }

    companion object {
        fun random(): Fruit = Fruit(FruitType.values().random())
    }
}
