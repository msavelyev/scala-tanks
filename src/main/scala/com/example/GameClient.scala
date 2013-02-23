package com.example

import org.cometd.bayeux.client.ClientSessionChannel
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener
import org.cometd.bayeux.{Message, Channel}
import org.cometd.client.BayeuxClient
import org.cometd.websocket.client.WebSocketTransport
import org.eclipse.jetty.websocket.WebSocketClientFactory
import scala.collection.mutable.Map
import java.util.{Map => JavaMap}
import scala.collection.JavaConversions._

class GameClient extends Loggable {

    var game: Game = null
    var client: BayeuxClient = null

    var okay = false

    def connect() {
        val webSocketClientFactory: WebSocketClientFactory = new WebSocketClientFactory()
        val transport = WebSocketTransport.create(Map[String, Object](), webSocketClientFactory)

        client = new BayeuxClient("http://localhost:8080/cometd-test/cometd", transport)

        client.getChannel(Channel.META_HANDSHAKE).addListener(
            (channel: ClientSessionChannel, message: Message) => {
                if (message.isSuccessful) {
                    connected()
                    okay = true
                }
            }
        )

        client.handshake()
    }

    def connected() {
        client.getChannel("/service/game/playerId").publish(Map[String, Any]())

        client.getChannel("/service/game/sendPlayerInfo").addListener(
            (channel: ClientSessionChannel, message: Message) => {
                val data: Map[String, AnyRef] = message.getDataAsMap
                INFO << "connected " + data
                val playerId: Int = data("playerId").toString.toInt
                if (playerId != game.playerId) {
                    game.connected(
                        playerId,
                        data("x").toString.toInt,
                        data("y").toString.toInt
                    )
                }
            }
        )

        client.getChannel("/service/game/sendPlayerId").addListener(
            (channel: ClientSessionChannel, message: Message) => {
                val data: Map[String, AnyRef] = message.getDataAsMap
                val players: Array[Any] = data("players").asInstanceOf[Array[Any]]
                for (player <- players) {
                    val data: JavaMap[String, Any] = Map[String, Any]("playerId" -> player.toString.toInt)
                    INFO << "getting info of player " + data
                    client.getChannel("/service/game/playerInfo").publish(data)
                }
                game.playerId = data("playerId").toString.toInt
                INFO << "got playerId " + game.playerId
            }
        )

        client.getChannel("/game/move").subscribe(
            (channel: ClientSessionChannel, message: Message) => {
                val data: Map[String, AnyRef] = message.getDataAsMap
                INFO << "move " + data
                val playerId: Int = data("playerId").toString.toInt
                if (playerId != game.playerId) {
                    game.move(
                        playerId,
                        data("dx").toString.toInt,
                        data("dy").toString.toInt
                    )
                }
            }
        )

        client.getChannel("/game/connected").subscribe(
            (channel: ClientSessionChannel, message: Message) => {
                val data: Map[String, AnyRef] = message.getDataAsMap
                INFO << "connected " + data
                val playerId: Int = data("playerId").toString.toInt
                if (playerId != game.playerId) {
                    game.connected(
                        playerId,
                        data("x").toString.toInt,
                        data("y").toString.toInt
                    )
                }
            }
        )

        client.getChannel("/game/disconnected").subscribe(
            (channel: ClientSessionChannel, message: Message) => {
                val data: Map[String, AnyRef] = message.getDataAsMap
                INFO << "disconnected " + data
                val playerId: Int = data("playerId").toString.toInt
                if (playerId != game.playerId) {
                    game.disconnected(playerId)
                }
            }
        )
    }

    def move(dx: Int, dy: Int) {
        if (okay) {
            val data: JavaMap[String, Any] = Map[String, Any]("dx" -> dx, "dy" -> dy)
            client.getChannel("/service/game/move").publish(data)
        }
    }

    implicit def messageListener(f: (ClientSessionChannel, Message) => Unit): MessageListener = {
        new MessageListener {
            def onMessage(channel: ClientSessionChannel, message: Message) {
                f(channel, message)
            }
        }
    }

}
