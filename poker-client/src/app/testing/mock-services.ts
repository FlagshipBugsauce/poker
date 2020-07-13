export class MockWebSocketService {
  public subscribeToGameListTopic(): void {
  }

  public subscribeToDrawnCardsTopic(gameId: string): void {
  }

  public onMessage(topic: string): void {
  }
}

export class MockCreateGameService {
  public subscribeToCreateGameTopic(): void {
  }
}

export class MockChatService {
  public subscribeToChatTopic(gameId: string = null) {
  }
}
