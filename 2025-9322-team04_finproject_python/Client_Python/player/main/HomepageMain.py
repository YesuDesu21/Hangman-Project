from Client_Python.player import controller
from Client_Python.player.controller.HomepageController import HomepageController

class HomepageMain:
    def __init__(self, username):
        self.username = username
        self.controller = HomepageController()
        print(f"\nHello {username}!\n")
        self.run()

    def run(self):
        while True:
            print(
                "Main Menu\n"
                "===============================\n"
                "1. Start game\n"
                "2. View Leaderboard\n"
                "3. Logout\n"
                "===============================\n"
            )

            if self.controller.session_expired.is_set():
                print("[INFO] Session expired. Returning to login screen...\n")
                from Client_Python.player.main.LoginMain import LoginMain
                LoginMain()
                break  # Exit this menu loop

            choice = input("Choose an option (1-3): ")
            try:
                choice = int(choice)
                self.controller.handle_choice(choice, self.username)
            except ValueError:
                print("Please enter a valid integer (1-3).\n")