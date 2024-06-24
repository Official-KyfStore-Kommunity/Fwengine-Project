import binaryandserializablemath as basm
import sys

if __name__ == "__main__":
    inputStr: str = sys.argv[1]
    print(basm.Binary.binary(inputStr))