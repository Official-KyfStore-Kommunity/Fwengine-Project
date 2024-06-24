from redvert import *
from binaryandserializablemath import *
import sys

if __name__ == "__main__":
    try:
        filename = sys.argv[1]
        filePath = sys.argv[2]
        with open(f"{filePath}\\{filename}", "r") as f:
            filecontent = f.read()
            filecontentB = Binary.binary(filecontent)

        with open(f"{filePath}\\{filename}", "w") as f:
            f.write(filecontentB)
    except:
        print("An error occurred whilst binaryifing the given data.")