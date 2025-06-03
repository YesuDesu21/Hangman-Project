from Client_Python.compilations import LeaderboardService_idl
LeaderboardService_idl._0_compilations.LeaderboardService

from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager

class LeaderboardsModel:
    def __init__(self):
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)

    def read_leaderboards(self):
        login_stub = self.manager.get_leaderboard_service()
        try:
            topPlayers = login_stub.getTopPlayers()
            player_data_list = []

            for topPlayer in topPlayers:
                # raw list of players and their scores
                #print(f"Player: {topPlayer.username}, Wins: {topPlayer.gamesWon}")
                player_data_list.append((topPlayer.username, topPlayer.gamesWon))

            return player_data_list
        except Exception as e:
            print(f"Error reading leaderboards: {e}")
            return []
