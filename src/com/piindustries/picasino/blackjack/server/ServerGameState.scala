package com.piindustries.picasino.blackjack.server

import com.piindustries.picasino.PiCasino
import com.piindustries.picasino.api.{
  GameState => APIGameState,
  GameEvent => APIGameEvent,
  NetworkHandler => APINetworkHandler
}
import com.piindustries.picasino.blackjack.domain.{
  GameEvent => BJGameEvent,
  GameEventType => BJGameEventType,
  Phase => BJPhase
}
import scala.collection.mutable
import com.piindustries.picasino.blackjack.domain.GameEvent

class ServerGameState(driver: PiCasino) extends APIGameState {
  // define class members who determine stat
  private val waitingList = mutable.MutableList.empty[String]
  private val players = mutable.MutableList.empty[PlayerData]
  private var phase: BJPhase = BJPhase.INITIALIZATION
  private val networkHandler: APINetworkHandler = driver.getNetworkHandler
  private var index = 0

  override def invoke(i: APIGameEvent) {
    // Sanity checks before further processing
    require(i.isInstanceOf[BJGameEvent])
    val event = i.asInstanceOf[BJGameEvent]
    val eType = event.getType
    val eVal = event.getValue

    // figure out how to handle event
    eType match {
      case BJGameEventType.ADD_PLAYER_TO_WAITING_LIST =>
        waitingList += (eVal.asInstanceOf[String])
        println("Waiting List:"+(waitingList mkString ","))
    }
  }

  def currentPlayer: PlayerData = players(index)
  class PlayerData(var username: String)
}