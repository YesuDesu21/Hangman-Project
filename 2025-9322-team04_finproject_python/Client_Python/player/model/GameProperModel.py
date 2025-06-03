from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager

class GameProperModel:
    def __init__(self, word: str):
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)
        #print(f"Model initialized with word: {word}") #TO CHECK WHAT WORD IS INITIALIZED
        self.word_to_guess = word.upper()
        self.revealed_letters = [False] * len(word)
        self.lives = 5
        self.timer_seconds = PlayerCorbaManager.get_instance().get_admin_service().getRoundDuration()

    def is_letter_in_word(self, letter: str) -> bool:
        return letter in self.word_to_guess

    def is_word_fully_revealed(self) -> bool:
        return all(self.revealed_letters)

    def decrement_lives(self):
        if self.lives > 0:
            self.lives -= 1

    def decrement_timer(self):
        if self.timer_seconds > 0:
            self.timer_seconds -= 1

    def get_word_to_guess(self) -> str:
        return self.word_to_guess

    def get_revealed_letters(self):
        return self.revealed_letters

    def get_lives(self) -> int:
        return self.lives

    def get_timer_seconds(self) -> int:
        return self.timer_seconds
