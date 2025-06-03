from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager
from Client_Python.player.model.GameProperModel import GameProperModel


class GameProperMain:
    def __init__(self, current_round, username):
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)
        self.word_service = self.manager.get_word_service()
        self.game_service = self.manager.get_game_service()
        self.username = username
        self.current_round = current_round

        # Get new word and game id
        self.actual_word, self.game_id = self.new_word()

        # Initialize model and controller
        from Client_Python.player.controller.GameProperController import GameProperController
        self.model = GameProperModel(self.actual_word)
        self.controller = GameProperController(username, current_round, self.game_id, self.actual_word)

        # Start game loop

        self.active = True
        self.run_round()

    def run_round(self):
        print(f"Round {self.current_round}")
       # self.setup_timer() # Thread
        while self.active:
            self.display_word()
            print(f"Lives: {self.controller.pass_lives()} || Time left: {self.controller.pass_timer()}")
            guess = input("Enter a letter to guess: ").strip().upper()
            self.controller.handle_guess(guess)
            print()

            if self.controller.signal_next_round:
                self.active = False  # reset activity
                GameProperMain(self.current_round + 1, self.username)


#==============================================================

    def display_word(self):
        revealed = self.controller.pass_revealed_letters()
        displayed = ""
        for i, ch in enumerate(self.actual_word):
            displayed += (ch + " ") if revealed[i] else "_ "
        print(f"Word: {displayed.strip()}")
        if all(revealed):
            self.active = False

    def new_word(self):
        word_info = self.game_service.getCurrentWordMask(self.username)
        if word_info is None or word_info.actualWord.strip() == "":
            print("Invalid word.")
            return "", ""
        return word_info.actualWord.upper(), word_info.gameId
