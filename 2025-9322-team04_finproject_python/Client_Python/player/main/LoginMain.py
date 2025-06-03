from Client_Python.player.controller.LoginController import LoginController

class LoginMain:
    def __init__(self):
        self.password = None
        self.username = None
        self.controller = LoginController()
        print("Welcome to LETTRBOX\n")
        self.start_login()

    def start_login(self):
        username = self.username = input("Enter username: ").strip()
        self.password = input("Enter password: ").strip()
        success = self.controller.set_information(self.username, self.password)
        if not success:
            self.start_login()
        if success:
            # Import here to avoid circular import at module load time
            from Client_Python.player.main.HomepageMain import HomepageMain
            HomepageMain(self.controller.model.manager.get_current_player_username())#passes username across the application
