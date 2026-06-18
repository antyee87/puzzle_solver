class LightsOutSolver (val size: Int) {
    /*
        GF(2) Gaussian Elimination
        Ax + b = 0
        A: effect
        x: operations
        b: board

        [0][0] ... [0][N]
          .   .      .
          .     .    .
          .       .  .
        [M][0] ... [M][N]
     */
    val A: MutableList<MutableList<ULong>>
    init {
        val segments = (size * size - 1) / 64 + 1
        A = MutableList(size * size) { MutableList<ULong>(segments) { 0u } }

        fun setBit(m: Int, n: Int) {
            var mask: ULong = 1u
            mask = mask shl (n % 64)
            A[m][n / 64] = A[m][n / 64] or mask
        }

        for (m in 0 until size) {
            for (n in 0 until size) {
                val pos = m * size + n
                setBit(pos, pos)
                if (m - 1 >= 0) setBit(pos, pos - size)
                if (m + 1 < size) setBit(pos, pos + size)
                if (n - 1 >= 0) setBit(pos, pos - 1)
                if (n + 1 < size) setBit(pos, pos + 1)
            }
        }

//        for (row in A) {
//            for (i in 0 until (size * size)) {
//                print(if ((row[i / 64] and (1uL shl i % 64)) > 0uL) 1 else 0)
//            }
//            print("\n")
//        }
//        print("\n")
    }

    fun solve (board: List<List<Boolean>>): List<List<Boolean>> {
        if (board.size != size) {
            throw IllegalArgumentException("Board shape has to equal $size * $size")
        }
        for (row in board) {
            if (row.size != size) {
                throw IllegalArgumentException("Board shape has to equal $size * $size")
            }
        }
        val b = board.flatten() as MutableList<Boolean>

        val rows = size * size
        val cols = size * size
        val segments = (size * size - 1) / 64 + 1

        var lead = 0
        for (r in 0 until rows) {
            if (lead >= cols) break

            var i = r
            while ((A[i][lead / 64] and (1uL shl (lead % 64))) == 0uL) {
                i++
                if (i == rows) {
                    i = r
                    lead++
                    if (lead == cols) break
                }
            }

            if (i != r) {
                val temp = b[r]
                b[r] = b[i]
                b[i] = temp

                for (s in 0 until segments) {
                    val temp = A[r][s]
                    A[r][s] = A[i][s]
                    A[i][s] = temp
                }
            }

            for (j in 0 until rows) {
                if (j != r && ((A[j][lead / 64] and (1uL shl (lead % 64))) > 0uL)) {
                    b[j] = b[j] xor b[r]
                    for (s in 0 until segments) {
                        A[j][s] = A[j][s] xor A[r][s]
                    }
                }
            }
            lead++
        }
        for (r in 0 until rows) {
            var allZeros = true
            for (s in 0 until segments) {
                if (A[r][s] > 0uL) {
                    allZeros = false
                    break
                }
            }
            if (allZeros && b[r]) {
                throw IllegalArgumentException("No solutions")
            }
        }
        return b.chunked(size)
    }
}