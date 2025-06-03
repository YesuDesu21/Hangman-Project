from Client_Python.player.model.LeaderboardsModel import LeaderboardsModel

class LeaderboardsController:
    def __init__(self):
        self.model = LeaderboardsModel()

    def show_leaderboard(self):
        return self.model.read_leaderboards()

    def record_player_win(self, username: str):
        self.model.record_player_win(username)
