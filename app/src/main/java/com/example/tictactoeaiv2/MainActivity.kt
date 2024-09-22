package com.example.tictactoeaiv2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tictactoeaiv2.ui.theme.TicTacToeAiV2Theme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeAiV2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    TicTacToeGame()
                }
            }
        }
    }
}

@Composable
fun TicTacToeGame() {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var statusMessage by remember { mutableStateOf("Player X's turn") }
    var gameEnded by remember { mutableStateOf(false) }
    var roundCounter by remember { mutableStateOf(0) }

    val playerMark = "X"
    val computerMark = "O"

    fun resetGame() {
        board = List(3) { MutableList(3) { "" } }
        currentPlayer = "X"
        statusMessage = "Player X's turn"
        gameEnded = false
        roundCounter = 0
    }

    fun checkWin(board: List<List<String>>, mark: String): Boolean {
        // Проверка победы для заданного маркера
        val lines = listOf(
            // Rows
            listOf(board[0][0], board[0][1], board[0][2]),
            listOf(board[1][0], board[1][1], board[1][2]),
            listOf(board[2][0], board[2][1], board[2][2]),
            // Columns
            listOf(board[0][0], board[1][0], board[2][0]),
            listOf(board[0][1], board[1][1], board[2][1]),
            listOf(board[0][2], board[1][2], board[2][2]),
            // Diagonals
            listOf(board[0][0], board[1][1], board[2][2]),
            listOf(board[0][2], board[1][1], board[2][0])
        )

        return lines.any { line -> line.all { it == mark } }
    }

    fun checkDraw(): Boolean {
        if (board.flatten().all { it.isNotEmpty() }) {
            statusMessage = "It's a draw!"
            return true
        }
        return false
    }

    fun makeComputerMove() {
        val move = hardModeComputer(board, computerMark, playerMark, roundCounter)
        if (move != null) {
            val (i, j) = move
            if (board[i][j].isEmpty()) {
                board = board.toMutableList().apply {
                    this[i] = this[i].toMutableList().apply {
                        this[j] = computerMark
                    }
                }
                roundCounter++
                if (checkWin(board, computerMark)) {
                    gameEnded = true
                    statusMessage = "Computer ($computerMark) wins!"
                } else if (checkDraw()) {
                    gameEnded = true
                    statusMessage = "It's a draw!"
                } else {
                    currentPlayer = playerMark
                    statusMessage = "Player $playerMark's turn"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusMessage,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Game Board
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .border(BorderStroke(1.dp, Color.Black))
                            .clickable(enabled = !gameEnded && board[i][j] == "" && currentPlayer == playerMark) {
                                board = board.toMutableList().apply {
                                    this[i] = this[i].toMutableList().apply {
                                        this[j] = playerMark
                                    }
                                }
                                roundCounter++
                                if (checkWin(board, playerMark)) {
                                    gameEnded = true
                                    statusMessage = "Player $playerMark wins!"
                                } else if (checkDraw()) {
                                    gameEnded = true
                                    statusMessage = "It's a draw!"
                                } else {
                                    currentPlayer = computerMark
                                    statusMessage = "Computer's turn"
                                    // Ход компьютера
                                    makeComputerMove()
                                }
                            }
                    ) {
                        Text(
                            text = board[i][j],
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { resetGame() }) {
            Text(text = "Restart")
        }
    }
}

fun checkWin(board: List<List<String>>, mark: String): Boolean {
    val lines = listOf(
        // Rows
        listOf(board[0][0], board[0][1], board[0][2]),
        listOf(board[1][0], board[1][1], board[1][2]),
        listOf(board[2][0], board[2][1], board[2][2]),
        // Columns
        listOf(board[0][0], board[1][0], board[2][0]),
        listOf(board[0][1], board[1][1], board[2][1]),
        listOf(board[0][2], board[1][2], board[2][2]),
        // Diagonals
        listOf(board[0][0], board[1][1], board[2][2]),
        listOf(board[0][2], board[1][1], board[2][0])
    )

    return lines.any { line -> line.all { it == mark } }
}


fun hardModeComputer(
    board: List<List<String>>,
    computerMark: String,
    playerMark: String,
    roundCounter: Int
): Pair<Int, Int>? {
    // 1. Проверка возможности выиграть в этот ход
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                val tempBoard = board.map { it.toMutableList() }
                tempBoard[i][j] = computerMark
                if (checkWin(tempBoard, computerMark)) {
                    return i to j
                }
            }
        }
    }

    // 2. Блокировка выигрыша игрока
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                val tempBoard = board.map { it.toMutableList() }
                tempBoard[i][j] = playerMark
                if (checkWin(tempBoard, playerMark)) {
                    return i to j
                }
            }
        }
    }

    // 3. Занять центр, если он свободен
    if (board[1][1].isEmpty()) {
        return 1 to 1
    }

    // 4. Занять противоположный угол
    val corners = listOf(0 to 0, 0 to 2, 2 to 0, 2 to 2)
    val oppositeCorners = mapOf(
        (0 to 0) to (2 to 2),
        (0 to 2) to (2 to 0),
        (2 to 0) to (0 to 2),
        (2 to 2) to (0 to 0)
    )
    for ((corner, opposite) in oppositeCorners) {
        val (i1, j1) = corner
        val (i2, j2) = opposite
        if (board[i1][j1] == playerMark && board[i2][j2].isEmpty()) {
            return i2 to j2
        }
    }

    // 5. Занять любой свободный угол
    val availableCorners = corners.filter { (i, j) -> board[i][j].isEmpty() }
    if (availableCorners.isNotEmpty()) {
        return availableCorners.random()
    }

    // 6. Занять любую свободную сторону
    val sides = listOf(0 to 1, 1 to 0, 1 to 2, 2 to 1)
    val availableSides = sides.filter { (i, j) -> board[i][j].isEmpty() }
    if (availableSides.isNotEmpty()) {
        return availableSides.random()
    }

    // 7. Если нет доступных ходов
    return null
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TicTacToeAiV2Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            TicTacToeGame()
        }
    }
}
