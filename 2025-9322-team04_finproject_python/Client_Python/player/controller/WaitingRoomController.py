# waiting_room_controller.py
import time
from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager
import threading
from Client_Python.player.main import HomepageMain, WaitingRoomMain
from Client_Python.player.main import GameProperMain
from Client_Python.player.model.WaitingRoomModel import WaitingRoomModel
from Client_Python.compilations.GameService_idl import _0_compilations
NoOpponentFoundException = _0_compilations.NoOpponentFoundException
GameNotFoundException = _0_compilations.GameNotFoundException

class WaitingRoomController:
    def __init__(self, username):
        self.model = WaitingRoomModel(username)
        orb_args = ["-ORBInitRef", "NameService=corbaloc::localhost:4321/NameService"]
        self.manager = PlayerCorbaManager.get_instance(orb_args)
        self.game_service = self.manager.get_game_service()
        self.current_round = 1
        self.game_started = False  # shared flag to stop all threads once started
        self.stop_threads = False

    #called by start_polling function from WaitingRoomMain
    def polling_loop(self):
        try:
            self.model.request_to_join_game(self.model.get_username())
            while True:
                status = self.model.get_game_status(self.model.get_username())
                if status.upper() == "STARTED":
                    self.game_started = True #sets to true
                    self.stop_threads = True

                    #open game proper
                    game = GameProperMain(
                        self.current_round,
                        self.model.get_username()
                    )
                    #print("Starting game Loop...")
                    game.run_round()
                    #print("Game Loop ended.") #DEBUG
                    break
                time.sleep(1)

        except NoOpponentFoundException:
            print("No opponent found. Try again.")
            self.stop_threads = True
            HomepageMain(self.model.get_username())
        except GameNotFoundException:
            print("Game not found.")
            self.stop_threads = True
            HomepageMain(self.model.get_username())
        except Exception as e:
            print(f"Error: {str(e)}")
            self.stop_threads = True
            HomepageMain(self.model.get_username())

    #called by start_countdown function from WaitingRoomMain
    def countdown(self):
        time_left = self.model.get_count_down_time()
        while time_left > 0:
            #print("DEBUG: IF GAME HAS STARTED " + str(self.game_started))  #FOR DEBUG
            if self.game_started:
                return  # STOPS COUNTDOWN IF GAME HAS STARTED
            print(f"Countdown: {time_left} seconds")
            time.sleep(1)
            time_left -= 1
            print()#move to next line


        # Countdown finished
        print("⏰ Countdown finished. Waiting for game to start...")
        def check_game_status():
            print("CHECK GAME STATUS THREAD")#FOR DEBUG
            try:
                time.sleep(3)
                status = self.game_service.getGameProgressStatus(self.model.get_username())#CHECK
                print(f"Status DEBUG: {status}")
                if status.lower() != "started":
                    print("⚠ Not enough players to start the game.")
                    self.stop_threads = True
                    option = input("Would you like to try again? (y/n): ").strip().lower()
                    if option == 'y':
                        WaitingRoomMain(self.model.get_username())  # You need to define this
                    else:
                        HomepageMain(self.model.get_username())
            except Exception as ex:
                print(f"Connection error: {ex}")
                HomepageMain(self.model.get_username())

        # Start a new thread to check game status after countdown
        threading.Thread(target=check_game_status).start()

    #called by start_polling_player_count function from WaitingRoomMain
    def polling_player_count(self):
        while self.model.get_count_down_time() > 0 and not self.game_started and not self.stop_threads:
            try:
                count = self.game_service.getCurrentPlayerCount()
                print(f"\rPlayers Joined: {count}")

                time.sleep(1)
            except Exception:
                print(-1)
