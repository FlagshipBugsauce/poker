package com.poker.poker.models.enums;

/**
 * Procedure to add a new emitter type: 1) Add ENUM 2) Add hash map in the constructor of SSE
 * service 3) Specify the timeout in SSE service 4) Specify the validator in requestEmitter method
 * 5) Specify which data to return on the refresh endpoint in SseController
 */
public enum EmitterType {
  GameList,
  Game,
  Lobby,
  Hand,
  GameData,
  PlayerData
}
