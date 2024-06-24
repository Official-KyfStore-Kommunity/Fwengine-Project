import sys
import os
import shutil

if __name__ == "__main__":
    scriptsPath: str = sys.argv[1]

    source: str = os.path.join(os.curdir, "assets\\programs\\Fwengine\\bin\\Debug\\net8.0\\Fwengine.dll")
    destination: str = os.path.join(scriptsPath, "Fwengine.dll")

    try:
        shutil.copyfile(source, destination)
        print("Successfully copied file!")
    except Exception as e:
        print(e)