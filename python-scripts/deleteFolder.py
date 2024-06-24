import shutil
import sys

def delete_folder(folderPath):
    try:
        shutil.rmtree(folder_path)
        print(f"Folder '{folder_path}' deleted successfully.")
    except FileNotFoundError:
        print(f"Folder '{folder_path}' not found.")
    except Exception as e:
        print(f"An error occurred while deleting the folder '{folder_path}': {e}")

if __name__ == "__main__":
    folder_path = sys.argv[1]
    delete_folder(folder_path)