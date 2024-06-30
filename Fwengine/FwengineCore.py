import subprocess

class FEConsole:
    def __init__(self) -> None:
        self.console = None

    def Println(self, input: str):
        self.console = subprocess.Popen(["cmd", "/c", "start", "cmd", "/k", "echo", input], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True, text=True)