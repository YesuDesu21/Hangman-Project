import threading
class WaitingRoomMain:

    def __init__(self, username):
        from Client_Python.player.controller.WaitingRoomController import WaitingRoomController
        print("WAITING AS: " + username)
        self.controller = WaitingRoomController(username)
        self.done_event = threading.Event()   # <- NEW

        #refer to java program for these callables
        self.start_countdown()
        self.start_polling()
        self.start_polling_player_count()

    def start_polling(self):
        threading.Thread(target=self.controller.polling_loop, daemon=True).start()

    def start_countdown(self):
        threading.Thread(target=self.controller.countdown, daemon=True).start()

    def start_polling_player_count(self):
        threading.Thread(target=self.controller.polling_player_count, daemon=True).start()

    def wait_until_done(self):
        print("[WaitingRoomMain] Waiting for game to finish...")
        self.done_event.wait()  # <- BLOCK here

    def mark_done(self):
        print("[WaitingRoomMain] Game finished, unblocking main thread.")
        self.done_event.set()  # <- SIGNAL here