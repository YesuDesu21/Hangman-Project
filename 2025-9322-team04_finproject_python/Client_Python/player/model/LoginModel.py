# Client_Python/player/LoginModel.py

from Client_Python.compilations.LoginService_idl import _0_compilations
LoginService = _0_compilations.LoginService
InvalidCredentialsException = _0_compilations.InvalidCredentialsException
UserAlreadyLoggedInException = _0_compilations.UserAlreadyLoggedInException

from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager

class LoginModel:
    def __init__(self):
        self.username = ""
        self.password = ""
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)

    def verify_player_credentials(self, username, password):
        try:
            result = self.manager.get_login_service().loginPlayer(username, password)
            success = result.success

            if success:
                self.manager.set_current_player_username(username)
                self.manager.set_session_id(result.sessionId)  # <-- Store session ID
                self.username = username
                self.password = password
                print(f"Player logged in: {username}")
                return True
            else:
                print(f"Login failed for: {username}")
                return False

        except InvalidCredentialsException:
            print("Error during login verification.")
            return False
        except UserAlreadyLoggedInException:
            print("User is already logged in.")
            return False

    def is_session_valid(self):
        session_id = PlayerCorbaManager.get_instance().get_session_id()
        try:
            return PlayerCorbaManager.get_instance().get_login_service().isSessionActive(session_id)
        except Exception as e:
            print(f"Session validation failed: {e}")
            return False
