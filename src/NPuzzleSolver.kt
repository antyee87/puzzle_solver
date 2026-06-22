import kotlin.math.abs

val moves = arrayOf(
    Triple(-1, 0, 0),
    Triple(0, 1, 1),
    Triple(1, 0, 2),
    Triple(0, -1, 3),
)
val oppositeMove = intArrayOf(2, 3, 0, 1)

val phaseTargets = arrayOf(
    setOf(1, 2, 3, 4),
    setOf(5, 6, 7, 8),
    setOf(9, 10, 11, 12, 13, 14, 15)
)
//val phaseTargets = arrayOf(
//    setOf(1, 2, 3, 4, 5, 6, 7, 8),
//    setOf(9, 10, 11, 12, 13, 14, 15)
//)

class SubPuzzleSolver(
    private val board: IntArray,
    private val activeTargets: Set<Int>,
    private val lockedIndices: Set<Int>
) {
    private val path = mutableListOf<Int>()
    private var isSolved = false

    // 局部启发式函数：只计算当前阶段关注的数字的曼哈顿距离
    private fun calculateSubHeuristic(currentBoard: IntArray): Int {
        var distance = 0
        for (i in currentBoard.indices) {
            val value = currentBoard[i]
            if (value in activeTargets) {
                val targetIndex = value - 1
                distance += abs(i % 4 - targetIndex % 4) + abs(i / 4 - targetIndex / 4)
            }
        }
        return distance
    }

    private fun isPhaseGoalReached(currentBoard: IntArray): Boolean {
        // 检查当前阶段的所有目标数字是否都到了最终正确位置
        return activeTargets.all { currentBoard[it - 1] == it }
    }

    fun solve(): List<Int>? {
        if (isPhaseGoalReached(board)) return emptyList()

        var bound = calculateSubHeuristic(board)
        val blankIndex = board.indexOf(0)

        // 限制单阶段迭代，防止死锁
        for (i in 0..100) {
            val nextBound = search(board, blankIndex, 0, bound, -1)
            if (isSolved) return path
            if (nextBound == Int.MAX_VALUE) return null
            bound = nextBound
        }
        return null
    }

    private fun search(currentBoard: IntArray, blankIndex: Int, g: Int, bound: Int, prevMoveDir: Int): Int {
        val h = calculateSubHeuristic(currentBoard)
        val f = g + h

        if (f > bound) return f
        if (isPhaseGoalReached(currentBoard)) {
            isSolved = true
            return 0
        }

        var minOverBound = Int.MAX_VALUE
        val blankX = blankIndex % 4
        val blankY = blankIndex / 4

        for (dirIndex in moves.indices) {
            if (prevMoveDir != -1 && dirIndex == oppositeMove[prevMoveDir]) continue

            val move = moves[dirIndex]
            val newX = blankX + move.second
            val newY = blankY + move.first

            if (newX in 0..3 && newY in 0..3) {
                val nextBlankIndex = newY * 4 + newX

                // 🔴 【核心剪枝】：如果移动会破坏前面阶段已经锁定的格子，直接禁止！
                if (nextBlankIndex in lockedIndices) continue

                currentBoard[blankIndex] = currentBoard[nextBlankIndex]
                currentBoard[nextBlankIndex] = 0
                path.add(move.third)

                val result = search(currentBoard, nextBlankIndex, g + 1, bound, dirIndex)

                if (isSolved) return 0

                if (result < minOverBound) minOverBound = result

                path.removeAt(path.size - 1)
                currentBoard[nextBlankIndex] = currentBoard[blankIndex]
                currentBoard[blankIndex] = 0
            }
        }
        return minOverBound
    }
}

class NPuzzleSolver {
    fun solve (board: List<List<Int>>): List<Int>? {
        if (board.size != 4) {
            throw IllegalArgumentException("Board shape has to equal 4 * 4")
        }
        for (row in board) {
            if (row.size != 4) {
                throw IllegalArgumentException("Board shape has to equal 4 * 4")
            }
        }
        val elements = mutableListOf<Int>()
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                if (board[r][c] !in 0..15) throw IllegalArgumentException("Board has invalid element ${board[r][c]}")
                if (board[r][c] in elements) throw IllegalArgumentException("Board has duplicated element ${board[r][c]}")
                elements.add(board[r][c])
            }
        }

        var flatList = mutableListOf<Int>()
        var blankRowFromBottom = 0
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val value = board[r][c]
                if (value == 0) {
                    blankRowFromBottom = 4 - r
                } else {
                    flatList.add(value)
                }
            }
        }

        var inversions = 0
        var step = 1
        var newFlatList = MutableList(flatList.size) {0}
        while (step < flatList.size) {
            var l = 0
            var i = 0
            while (l < flatList.size) {
                val m = l + step
                var r = m
                while (l < m && l < flatList.size && r < m + step && r < flatList.size) {
                    if (flatList[l] <= flatList[r]) {
                        newFlatList[i++] = flatList[l++]
                    }
                    else {
                        newFlatList[i++] = flatList[r++]
                        inversions += m - l
                    }
                }
                while (l < m && l < flatList.size) {
                    newFlatList[i++] = flatList[l++]
                }
                while (r < m + step && r < flatList.size) {
                    newFlatList[i++] = flatList[r++]
                }
                l = m + step
            }
            val temp = flatList
            flatList = newFlatList
            newFlatList = temp
            step *= 2
        }

        if (blankRowFromBottom % 2 == 0) {
            if (inversions % 2 == 0) {
                throw IllegalArgumentException("No solutions")
            }
        } else {
            if (inversions % 2 == 1) {
                throw IllegalArgumentException("No solutions")
            }
        }

        val currentBoard = board.flatten().toIntArray()
        val totalPath = mutableListOf<Int>()
        val lockedIndices = mutableSetOf<Int>()

//        println("=== 开始分治法求解 ===")

        for (phase in phaseTargets.indices) {
            val targets = phaseTargets[phase]
//            println("▶ 阶段 ${phase + 1}: 正在还原数字 $targets ...")

            val subSolver = SubPuzzleSolver(currentBoard, targets, lockedIndices)
            val subPath = subSolver.solve()

            if (subPath == null) {
//                println("❌ 阶段 ${phase + 1} 遭遇死胡同（无法在不破坏已锁定格子的情况下解开此阶段）。")
                return null
            }

//            println("  -> 成功！本阶段耗步数: ${subPath.size}")
            totalPath.addAll(subPath)

            // 将已经还原好的一整行/区域的索引加入锁定集合，后面的阶段绝不准踩
            targets.forEach { value ->
                lockedIndices.add(value - 1)
            }
        }

        return totalPath
    }
}