package ru.spbstu.competition

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import ru.spbstu.competition.game.Intellect
import ru.spbstu.competition.game.State
import ru.spbstu.competition.protocol.Protocol
import ru.spbstu.competition.protocol.data.*

object Arguments {
    @Option(name = "-u", aliases = arrayOf("Specify server url"))
    var url: String = "91.151.191.57"

    @Option(name = "-p", aliases = arrayOf("Specify server port"))
    var port: Int = 50006

    fun use(args: Array<String>): Arguments =
            CmdLineParser(this).parseArgument(*args).let{ this }
}

fun main(args: Array<String>) {
    Arguments.use(args)

    println("Whassup, nigga! I'm broke in!")

    val protocol = Protocol(Arguments.url, Arguments.port)  //Создаем протокол для обмена с сервером
    val gameState = State()                                 //Храним состояние игрового поля
    val intellect = Intellect(gameState, protocol)          //Функционал йо бота

    protocol.handShake("Mitya in da house!")
    val setupData = protocol.setup()
    gameState.init(setupData)

    println("Received id = ${setupData.punter}")

    protocol.ready()

    gameloop@ while(true) {
        val message = protocol.serverMessage()
        when(message) {
            is GameResult -> {
                println("HAHAHA, YOU LOST WITH THE BEGINNING")
                val myScore = message.stop.scores[protocol.myId]
                println("MY SCORE ${myScore.score} POINTS, YEAH!")
                break@gameloop
            }
            is Timeout -> {
                println("It's not I who read fast, it's you who think slowly!")
            }
            is GameTurnMessage -> {
                for(move in message.move.moves) {
                    when(move) {
                        is PassMove -> {}
                        is ClaimMove -> gameState.update(move.claim)
                    }
                }
            }
        }

        println("Fick off, I'll make a move!")
        intellect.makeMove()
    }
}
