from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager

class GameResultModel:
    def __init__(self):
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)
        self.result = None  # 0 = Win, 1 = Lose

    def fetch_game_result(self, username):
        try:
            game_service = self.manager.get_game_service()
            # Assume server method: getGameResult(username) returns 0 or 1
            self.result = game_service.getGameResult(username)
            return self.result
        except Exception as e:
            print(f"Error fetching game result from server: {e}")
            return None

    def get_result_message(self):
        messages = ["You Won!", "Game Over!"]
        if self.result is not None and 0 <= self.result < len(messages):
            return messages[self.result]
        return "Result unknown"
