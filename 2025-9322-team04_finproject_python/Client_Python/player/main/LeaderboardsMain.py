from Client_Python.player.controller.LeaderboardsController import LeaderboardsController

class LeaderboardsMain:
    def __init__(self):
        self.controller = LeaderboardsController()
        self.display_leaderboard()

    def display_leaderboard(self):
        print("\n========== LEADERBOARD ==========")
        print("Rank |    Player Name    | Score")
        print("-----|-------------------|-------")

        leaderboard_data = self.controller.show_leaderboard()
        for i, (username, games_won) in enumerate(leaderboard_data, start=1):
            print(f"{i:<5}| {username:<18}| {games_won}\n=================================\n")
