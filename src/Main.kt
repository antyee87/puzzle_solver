fun printPipesBoard(board: List<List<Int>>) {
    for (r in board.indices) {
        for (c in board[r].indices) {
            val p = board[r][c]
            val char = when (p) {
                8 -> "上"
                4 -> "右"
                2 -> "下"
                1 -> "左"
                6 -> "┏"
                3  -> "┓"
                9  -> "┛"
                12  -> "┗"
                10 -> "┃"
                5  -> "━"
                14 -> "┣"
                7  -> "┳"
                11 -> "┫"
                13 -> "┻"
                15 -> "╋"
                0  -> " "
                else -> "•"
            }
            print("$char ")
        }
        println()
    }
    println("-----------------")
}

fun main() {
    // Lights Out Solver
    run {
        val intBoard: List<List<Int>> = listOf(
            listOf(0, 0, 0, 0, 0),
            listOf(0, 0, 1, 1, 1),
            listOf(1, 0, 1, 0, 1),
            listOf(0, 0, 1, 1, 1),
            listOf(1, 0, 1, 1, 0)
        )
        val boolBoard: List<List<Boolean>> = intBoard.map { row ->
            row.map { element -> element == 1 }
        }
        val lightsOutSolver = LightsOutSolver(5)
        val result = lightsOutSolver.solve(boolBoard)
        for (row in result) {
            for (bit in row) {
                print("${if (bit) "1" else "0"} ")
            }
            print("\n")
        }
    }
    // N-Puzzle Solver
    run {
        val board: List<List<Int>> = listOf(
            listOf(5, 15, 1, 4),
            listOf(2, 10, 3, 7),
            listOf(9, 0, 11, 8),
            listOf(13, 6, 14, 12),
        )
        val nPuzzleSolver = NPuzzleSolver()
        val solution = nPuzzleSolver.solve(board)
        if (solution != null) {
            for (i in solution.indices) {
//                print(when(solution[i]) {
//                    0 -> "下"
//                    1 -> "左"
//                    2 -> "上"
//                    3 -> "右"
//                    else -> ""
//                })
                print(when(solution[i]) {
                    0 -> "上"
                    1 -> "右"
                    2 -> "下"
                    3 -> "左"
                    else -> ""
                })
                if ((i + 1) % 5 == 0) {
                    print("\n")
                }
            }
        }
    }
    // Pipe Puzzle Solver
    run {
        val board = listOf(
            listOf(2, 3, 5, 6, 2),
            listOf(3, 3, 1, 3, 7),
            listOf(12, 4, 5, 3, 6),
            listOf(14, 10, 6, 6, 12),
            listOf(6, 5, 5, 10, 9),
        )
        printPipesBoard(board)
        val pipesPuzzleSolver = PipesPuzzleSolver(5, 5)
        val result = pipesPuzzleSolver.solve(board)
        printPipesBoard(result)
    }
}