class HomepageModel:
    def __init__(self):
        self.username = ""
        self.waiting_time = 0  # Countdown time in seconds

    def get_username(self):
        return self.username

    def set_username(self, username):
        self.username = username

    def get_waiting_time(self):
        return self.waiting_time

    def set_waiting_time(self, waiting_time):
        self.waiting_time = waiting_time

    def is_session_valid(self, corba_manager):
        session_id = corba_manager.get_session_id()
        return corba_manager.get_login_service().isSessionActive(session_id)