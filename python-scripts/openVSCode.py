import sys
import subprocess
import shutil
import tkinter as tk
from tkinter import messagebox

def is_vscode_installed():
    # Check if 'code' command is executable
    return shutil.which('code') is not None

def open_file_with_vscode(file_path):
    if not is_vscode_installed():
        root = tk.Tk()
        root.withdraw()  # Hide the main window
        
        messagebox.showerror("Program Not Found", 
                             "Sorry, but currently you do not have \"VSCODE\" installed on your system. "
                             "Please install this to edit your script with this preference.")
        return
    
    try:
        subprocess.Popen(["code", file_path], shell=True)
    except subprocess.CalledProcessError as e:
        # Handle any specific errors if needed
        print(f"Error opening file with Visual Studio Code: {e}")

if __name__ == "__main__":
    file_path = sys.argv[1]
    open_file_with_vscode(file_path)
