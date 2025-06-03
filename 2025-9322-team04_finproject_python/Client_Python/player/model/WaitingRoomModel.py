# waiting_room_model.py
from Client_Python.compilations import AdminService_idl
AdminService_idl._0_compilations.AdminService
from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager


class WaitingRoomModel:
    _instance = None  # Define class-level singleton holder
    def __init__(self, username):
        self.count_down_time = None
        self.username = username
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)

    def get_username(self):
        return self.username

    def request_to_join_game(self, username):
        stub = self.manager.get_game_service()
        stub.requestToJoinGame(username)

    def get_game_status(self, username):
        stub = self.manager.get_game_service()
        return stub.getGameProgressStatus(username)

    def get_current_word_mask(self, username):
        stub = self.manager.get_game_service()
        return stub.getCurrentWordMask(username)

    def get_count_down_time(self):
        stub = self.manager.get_admin_service()
        self.count_down_time = stub.getJoinTimeout()
        return self.count_down_time
