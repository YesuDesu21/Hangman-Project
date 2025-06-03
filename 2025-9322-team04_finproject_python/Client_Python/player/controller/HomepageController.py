from Client_Python.player.main.WaitingRoomMain import WaitingRoomMain
from Client_Python.player.main.LeaderboardsMain import LeaderboardsMain
from Client_Python.player.model import HomepageModel
from Client_Python.manager.PlayerCorbaManager import PlayerCorbaManager

import threading
import time
import sys

class HomepageController:
    def __init__(self):
        self.model = HomepageModel()
        self.corba_manager = PlayerCorbaManager.get_instance()

        # Start session validation timer
        self.session_timer_thread = None
        self.stop_session_check = threading.Event()
        self.session_expired = threading.Event()
        self.start_session_validation_timer()


    def handle_choice(self, choice, username):
        if choice == 1:
            print("-----Waiting Room-----\n")
            waiting_room = WaitingRoomMain(username) # Open Waiting Room
            waiting_room.wait_until_done()
            print("past waiting room main callable")#debug

        elif choice == 2:
            print("Viewing leaderboards...\n")
            LeaderboardsMain()  # Open Leaderboards
        elif choice == 3:
            print("Logging out...")

            self.stop_session_check.set()
            try:
                session_id = self.corba_manager.get_session_id()
                self.corba_manager.get_login_service().logoutPlayer(session_id)
                print(f"[INFO] Logged out session: {session_id}")
            except Exception as e:
                print(f"[ERROR] Failed to logout from server: {e}")

            from Client_Python.player.main.LoginMain import LoginMain
            LoginMain()
            raise SystemExit()
        else:
            print("Invalid choice, please select a valid option.")

    def start_session_validation_timer(self):
        def check_session():
            while not self.stop_session_check.is_set():
                try:
                    if not self.model.is_session_valid(self.corba_manager):
                        print("\n[WARNING] Your session has expired or you have been logged out elsewhere.\n")

                        session_id = self.corba_manager.get_session_id()
                        try:
                            self.corba_manager.get_login_service().logoutPlayer(session_id)
                        except Exception as logout_err:
                            print(f"[ERROR] Failed to logout from server: {logout_err}")

                        self.stop_session_check.set()
                        self.session_expired.set()  # <== notify main thread

                        return
                except Exception as e:
                    print(f"[ERROR] Session validation failed: {e}")
                time.sleep(5)

        self.session_timer_thread = threading.Thread(target=check_session, daemon=True)
        self.session_timer_thread.start()
