import subprocess as sbp

class FEConsole:
    def __init__(self) -> None:
        self.console = None

    def Println(self, input: str):
        self.console = sbp.Popen(["cmd", "/c", "start", "cmd", "/k", "echo", input], stdin=sbp.PIPE, stdout=sbp.PIPE, stderr=sbp.PIPE, shell=True, text=True)