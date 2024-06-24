import os
import sys
import shutil

if __name__ == "__main__":
    parentFolderPath = sys.argv[1]
    binFolderPath = f"{parentFolderPath}\\bin"
    assetFolderPath = f"{binFolderPath}\\assets"
    imageFolderPath = f"{assetFolderPath}\\images"
    if os.path.exists(binFolderPath):
        if os.path.exists(binFolderPath + "\\assets"):
            if os.path.exists(binFolderPath + "\\assets\\images"):
                mainDir = "assets\\images\\defaultSprite1.png"
                finalDir = imageFolderPath + "\\defaultSprite.png"
                shutil.copyfile(mainDir, finalDir)
            else:
                os.mkdir(imageFolderPath)
                mainDir = "assets\\images\\defaultSprite1.png"
                finalDir = imageFolderPath + "\\defaultSprite.png"
                shutil.copyfile(mainDir, finalDir)
        else:
            os.makedirs(imageFolderPath, exist_ok=True)
            mainDir = "assets\\images\\defaultSprite1.png"
            finalDir = imageFolderPath + "\\defaultSprite.png"
            shutil.copyfile(mainDir, finalDir)
    else:
        os.makedirs(imageFolderPath, exist_ok=True)
        mainDir = "assets\\images\\defaultSprite1.png"
        finalDir = imageFolderPath + "\\defaultSprite.png"
        shutil.copyfile(mainDir, finalDir)