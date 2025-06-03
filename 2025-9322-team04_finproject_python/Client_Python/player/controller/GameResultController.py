from Client_Python.player.main import LeaderboardsMain, HomepageMain

class GameResultController:
    def __init__(self, model):
        self.model = model

    # create gamedetails after game ends
    def create_game_details(self):
        self.model.create_game_details

    #update game after game ends
    def update_game(self, status):
        self.model.update_game