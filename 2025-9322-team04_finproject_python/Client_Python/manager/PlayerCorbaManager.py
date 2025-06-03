import sys
import os
import threading
import traceback

import CORBA
import CosNaming

# Ensure the parent directory is in the path for imports
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

# CORBA IDL-generated stubs
from Client_Python.compilations import LoginService_idl
from Client_Python.compilations import GameService_idl
from Client_Python.compilations import AdminService_idl
from Client_Python.compilations import LeaderboardService_idl
from Client_Python.compilations import WordService_idl
from Client_Python.compilations import GameManagerService_idl


class PlayerCorbaManager:
    _instance = None
    _lock = threading.Lock()

    def __init__(self, orb_args):
        try:
            # Initialize ORB
            self.orb = CORBA.ORB_init(orb_args, "")

            # Resolve naming service
            obj_ref = self.orb.resolve_initial_references("NameService")
            self.naming_context = obj_ref._narrow(CosNaming.NamingContextExt)

            # Resolve and connect to all services using _0_compilations._objref_*
            obj = self.naming_context.resolve_str("LoginService")
            self.login_service = obj._narrow(LoginService_idl._0_compilations.LoginService)
            self._validate_service(self.login_service, "LoginService")
            print()

            obj = self.naming_context.resolve_str("GameService")
            self.game_service = obj._narrow(GameService_idl._0_compilations.GameService)
            self._validate_service(self.game_service, "GameService")

            obj = self.naming_context.resolve_str("AdminService")
            self.admin_service = obj._narrow(AdminService_idl._0_compilations.AdminService)
            self._validate_service(self.admin_service, "AdminService")

            obj = self.naming_context.resolve_str("LeaderboardService")
            self.leaderboard_service = obj._narrow(LeaderboardService_idl._0_compilations.LeaderboardService)
            self._validate_service(self.leaderboard_service, "LeaderboardService")

            obj = self.naming_context.resolve_str("WordService")
            self.word_service = obj._narrow(WordService_idl._0_compilations.WordService)
            self._validate_service(self.word_service, "WordService")

            obj = self.naming_context.resolve_str("GameManagerService")
            self.game_manager_service = obj._narrow(GameManagerService_idl._0_compilations.GameManagerService)
            self._validate_service(self.game_manager_service, "GameManagerService")

            self.current_player_username = None
            print("✔ PlayerCorbaManager: Connected to all services.")

        except Exception as e:
            print("X PlayerCorbaManager: Failed to connect to services.", file=sys.stderr)
            traceback.print_exc()

    @classmethod
    def get_instance(cls, orb_args=None):
        with cls._lock:
            if cls._instance is None:
                if orb_args is None:
                    raise ValueError("orb_args required for first initialization")
                cls._instance = cls(orb_args)
        return cls._instance

    def _validate_service(self, service, name):
        if service is None:
            raise RuntimeError(f"{name} is not available via CORBA naming service.")

    # Username management
    def set_current_player_username(self, username):
        #print("set_current_player_username", username)
        self.current_player_username = username

    def get_current_player_username(self):
        #print("get_current_player_username")
        return self.current_player_username

    # Service getters
    def get_login_service(self):
        #print("get_login_service")
        return self.login_service

    def get_game_service(self):
        #print("get_game_service")
        return self.game_service

    def get_admin_service(self):
        #print("get_admin_service")
        return self.admin_service

    def get_leaderboard_service(self):
        #print("get_leaderboard_service")
        return self.leaderboard_service

    def get_word_service(self):
        #print("get_word_service")
        return self.word_service

    def get_game_manager_service(self):
        #print("get_game_manager_service")
        return self.game_manager_service

    def get_session_id(self):
        return self.session_id

    def set_session_id(self, session_id):
        self.session_id = session_id

    # Optional ORB shutdown method
    def shutdown(self):
        try:
            self.orb.shutdown(wait=False)
            print("ORB shutdown completed.")
        except Exception as e:
            print("Failed to shut down ORB:", e)
