from Client_Python.player.model.LoginModel import LoginModel

class LoginController:
    def __init__(self):
        self.model = LoginModel()

    def set_information(self, username, password):
        self.username = username
        self.password = password
        success = self.model.verify_player_credentials(username, password)
        validate = self.model.is_session_valid()
        if success and validate:
            print("Login successful.")
            return True
        else:
            print("Login failed.")
            return False
