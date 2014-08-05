package com.piindustries.picasino.blackjack.client

import com.piindustries.picasino.PiCasino
import com.piindustries.picasino.api.{
  GameState 		=> APIGameState,
  GameEvent 		=> APIGameEvent,
  NetworkHandler 	=> APINetworkHandler
}

class ClientGameState(driver: PiCasino, username: String) extends APIGameState {
  val networkHandler = driver.getNetworkHandler

	def invoke(i: APIGameEvent) = ???
}
