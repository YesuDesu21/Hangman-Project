import threading
import time
from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager
from Client_Python.player.main import GameProperMain
from Client_Python.player.main.HomepageMain import HomepageMain
from Client_Python.player.model.GameProperModel import GameProperModel

class GameProperController:
    def __init__(self, username, current_round, game_id, actual_word):
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)
        self.model = GameProperModel(actual_word)
        self.game_id = game_id
        self.username = username
        self.has_submitted = False
        self.signal_next_round = False
        self.next_round_started = False
        self.current_round = current_round
        self.max_rounds = 3
        self.setup_timer()

    def setup_timer(self):
        def game_timer_loop():
            while self.model.get_timer_seconds() > 0 and not self.has_submitted and self.model.get_lives() > 0:
                time.sleep(1)
                self.model.decrement_timer()
               # print(
                   # f"Time Remaining: {self.model.get_timer_seconds()}")

            if not self.has_submitted and (self.model.get_timer_seconds() <= 0 or self.model.get_lives() <= 0):
                self.has_submitted = True
                self.notify_player_done()
                print("Waiting for others...")

        timer_thread = threading.Thread(target=game_timer_loop, daemon=True)
        timer_thread.start()

    def handle_guess(self, guess):
        if self.has_submitted or self.model.get_lives() <= 0:
            return
        if len(guess) != 1 or not guess.isalpha():
            print("Please enter a single alphabetic character.")
            return

        print(f"You entered {guess}")
        is_correct = self.model.is_letter_in_word(guess)
        if is_correct:
            print("Your guess is correct")
        else:
            print("Your guess is wrong")
            self.model.decrement_lives()

        self.update_revealed_letters(guess)

        if not self.has_submitted and self.model.is_word_fully_revealed():
            self.has_submitted = True
            self.notify_player_done()
            print("You have completed the word.\nWaiting for others...")

        if not self.has_submitted and (self.model.get_timer_seconds() <= 0 or self.model.get_lives() <= 0):
            self.has_submitted = True
            self.notify_player_done()
            print("Time or lives exhausted.\nWaiting for others...")

    def update_revealed_letters(self, guess):
        actual_word = self.model.get_word_to_guess()
        revealed = self.model.get_revealed_letters()
        for i, ch in enumerate(actual_word):
            if ch == guess:
                revealed[i] = True

    def notify_player_done(self):
        try:
            guessed_correctly = self.model.is_word_fully_revealed()
            game_service = self.manager.get_game_service()
            server_round_number = game_service.getCurrentRound(self.game_id)
            game_service.setPlayerDone(self.username, server_round_number, guessed_correctly)
            game_service.advanceToNextRoundIfReady(self.username, server_round_number)
            self.check_all_players_done()
        except Exception as e:
            print(f"Error notifying server: {e}")

    def check_all_players_done(self):
        def wait_loop():
            while True:
                try:
                    game_service = self.manager.get_game_service()
                    if game_service.isGameOver():
                        self.show_final_scores_and_return_home()
                        break

                    server_round = game_service.getCurrentRound(self.game_id)
                    if server_round > self.current_round:
                        self.proceed_to_next_round()
                        break

                    if game_service.areAllPlayersDone(self.username, self.current_round):
                        if self.current_round >= self.max_rounds - 1:
                            self.end_game()
                        else:
                            self.show_final_word_and_countdown()
                        break
                except Exception as ex:
                    print(f"Error checking players: {ex}")
                time.sleep(1)

        threading.Thread(target=wait_loop, daemon=True).start()

    def show_final_word_and_countdown(self):
        countdown_seconds = 5

        def countdown_timer():
            seconds_left = countdown_seconds
            while seconds_left > 0:
                actual_word = self.model.get_word_to_guess()
                revealed = self.model.get_revealed_letters()
                masked_word = ''.join([ch if revealed[i] else '_' for i, ch in enumerate(actual_word)])
                print(f"The word was: {masked_word}\nNext round in {seconds_left} seconds")
                time.sleep(1)
                seconds_left -= 1
            self.wait_for_next_round_on_server()

        threading.Thread(target=countdown_timer, daemon=True).start()

    def wait_for_next_round_on_server(self):
        def check_server():
            while True:
                try:
                    game_service = self.manager.get_game_service()
                    server_round = game_service.getCurrentRound(self.game_id)
                    if server_round > self.current_round:
                        self.proceed_to_next_round()
                        break
                except Exception as ex:
                    print(f"Error waiting for next round: {ex}")
                time.sleep(1)

        threading.Thread(target=check_server, daemon=True).start()

    def proceed_to_next_round(self):
        if self.next_round_started:
            return
        self.next_round_started = True
        self.has_submitted = False
        self.signal_next_round = True
        #next_round = self.current_round + 1

        #GameProperMain(next_round, self.username)

    def end_game(self):
        print("Game Over!\nThe final word was: " + self.model.get_word_to_guess())
        print("Thank you for playing!")

    def show_final_scores_and_return_home(self):
        try:
            game_service = self.manager.get_game_service()
            scores = game_service.getScoreSummary()
            winner = game_service.getWinner()
            print("Game Over!\nWinner: " + winner + "\n\n" + scores)
            HomepageMain(self.username)
        except Exception as ex:
            print(f"Error retrieving final scores: {ex}")

#--------functions to pass to Main------------

#For display_word
    def pass_revealed_letters(self):
        return self.model.get_revealed_letters()
#For lives and timer
    def pass_timer(self):
        return self.model.get_timer_seconds()

    def pass_lives(self):
        return self.model.get_lives()