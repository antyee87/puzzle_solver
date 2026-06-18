const val UP = 8
const val RIGHT = 4
const val DOWN = 2
const val LEFT = 1

class PipesPuzzleSolver (
    val width: Int,
    val height: Int
) {
    // 上右下左 #### 4bit
    // 0 1 2 3
    // 1: 有開口 0: 無開口
    // 最终存储正确解法的数组
    private val resultGrid = IntArray(width * height)
    private var isSolved = false

    /**
     * 顺时针旋转水管 90 度
     */
    private fun rotateClockwise(pipe: Int): Int {
        val lastBit = pipe and 1
        return (pipe shr 1) or (lastBit shl 3)
    }

    /**
     * 核心检测函数：检查当前刚放好的格子 (x, y) 与它已经填好的左边、上边邻居是否匹配
     * 同时检查它是否朝向了外墙。
     */
    private fun isValidPlacement(x: Int, y: Int, pipe: Int): Boolean {
        // 1. 边界检查：边缘水管绝对不能朝墙壁开口
        if (y == 0 && (pipe and UP) != 0) return false
        if (y == height - 1 && (pipe and DOWN) != 0) return false
        if (x == 0 && (pipe and LEFT) != 0) return false
        if (x == width - 1 && (pipe and RIGHT) != 0) return false

        // 2. 与上方邻居对齐检查 (如果上面有格子的话)
        if (y > 0) {
            val upNeighbor = resultGrid[(y - 1) * 5 + x]
            val upHasDownOpen = (upNeighbor and DOWN) != 0
            val currentHasUpOpen = (pipe and UP) != 0
            if (upHasDownOpen != currentHasUpOpen) return false // 接口对不上，漏水！
        }

        // 3. 与左边邻居对齐检查 (如果左边有格子的话)
        if (x > 0) {
            val leftNeighbor = resultGrid[y * width + (x - 1)]
            val leftHasRightOpen = (leftNeighbor and RIGHT) != 0
            val currentHasLeftOpen = (pipe and LEFT) != 0
            if (leftHasRightOpen != currentHasLeftOpen) return false // 接口对不上，漏水！
        }

        return true
    }
    fun solve (board: List<List<Int>>): List<List<Int>> {
        if (board.size != 5) {
            throw IllegalArgumentException("Board shape has to equal $5 * $5")
        }
        for (row in board) {
            if (row.size != 5) {
                throw IllegalArgumentException("Board shape has to equal $5 * $5")
            }
        }
        dfs(board.flatten().toIntArray(),0, 0, )
        if (isSolved) {
            return resultGrid.toList().chunked(width)
        }
        else {
            throw IllegalArgumentException("No solution")
        }
    }

    /**
     * DFS 逐格搜索
     */
    private fun dfs(board: IntArray, x: Int, y: Int) {
        // 如果已经成功填满最后一个格子的下一格，说明全盘解开
        if (y == height) {
            isSolved = true
            return
        }

        // 计算下一个格子的坐标
        val nextX = (x + 1) % width
        val nextY = if (nextX == 0) y + 1 else y

        val idx = y * width + x
        var currentPipe = board[idx]

        // 每个格子尝试 4 种可能的旋转角度 (0度, 90度, 180度, 270度)
        for (rot in 0 until 4) {
            if (isValidPlacement(x, y, currentPipe)) {
                // 暂时把这个合法的旋转放入结果盘面
                resultGrid[idx] = currentPipe

                // 继续递归填下一个格子
                dfs(board,nextX, nextY)

                // 如果在后续递归中解开了，一路返回
                if (isSolved) return
            }

            // 如果不合法，或者后续卡死了，则顺时针转 90 度继续测试下一种角度
            currentPipe = rotateClockwise(currentPipe)
        }
    }
}