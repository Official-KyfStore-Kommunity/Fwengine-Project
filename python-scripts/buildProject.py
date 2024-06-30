import json
import re
import sys
import shutil
import subprocess
import os
import glob
import logging
from time import perf_counter

def setup_logging(log_path):
    open(log_path, 'w').close()
    logging.basicConfig(
        filename=log_path,
        level=logging.DEBUG,
        format='%(asctime)s - %(levelname)s - %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )

def compile_into_exe(py_file_path, output_dir):
    try:
        nuitka_cmd = [
            "python", "-m", "nuitka",
            "--windows-disable-console",
            f"--output-dir={output_dir}",
            py_file_path
        ]
        logging.info("Compiling with Nuitka: %s", ' '.join(nuitka_cmd))
        subprocess.run(nuitka_cmd, check=True)
        logging.info("Compilation successful.")
    except subprocess.CalledProcessError as e:
        logging.error("Compilation failed: %s", e)
        raise

def delete_file(file_path):
    try:
        os.remove(file_path)
        logging.info("File '%s' deleted successfully.", file_path)
    except FileNotFoundError:
        logging.warning("File '%s' not found.", file_path)
    except Exception as e:
        logging.error("Error deleting file '%s': %s", file_path, e)

def run_exe(exe_path):
    try:
        subprocess.run([exe_path], shell=True, check=True)
        logging.info("Executable '%s' executed successfully.", exe_path)
    except subprocess.CalledProcessError as e:
        logging.error("Error running executable '%s': %s", exe_path, e)
    except Exception as e:
        logging.error("An error occurred while running '%s': %s", exe_path, e)

def delete_folder(folder_path):
    try:
        shutil.rmtree(folder_path)
        logging.info("Folder '%s' deleted successfully.", folder_path)
    except FileNotFoundError:
        logging.warning("Folder '%s' not found.", folder_path)
    except Exception as e:
        logging.error("Error deleting folder '%s': %s", folder_path, e)

def build_project_with_data(name_of_scene, scene_sprites, scripts_in_scene, parent_folder, py_file_path):
    data = f"""
import tkinter as tk

def demanipulate_pos(mani_str):
    mani_str = str(mani_str)
    len_first_number = int(mani_str[0])
    first_number = mani_str[1:1 + len_first_number]
    len_second_number = int(mani_str[1 + len_first_number])
    second_number = mani_str[2 + len_first_number:2 + len_first_number + len_second_number]
    return f"{{first_number}}|{{second_number}}"

def create_sprites():
    root = tk.Tk()
    root.title("{name_of_scene}")
    root.config(bg="#3e3e3e")
    root.iconbitmap('assets/images/cogs.ico')
    root.geometry("675x225")
    root.minsize(width=675, height=225)
    root.maxsize(width=675, height=225)

    default_sprite_path = r"{parent_folder}/bin/assets/images/defaultSprite.png"
    sprite_image = tk.PhotoImage(file=default_sprite_path)

    sprite_positions = {scene_sprites}

    canvas = tk.Canvas(root, width=675, height=225, bg="#3e3e3e")
    canvas.pack()

    for sprite, pos in sprite_positions.items():
        new_pos = demanipulate_pos(pos)
        x_pos, y_pos = new_pos.split('|')
        canvas.create_image(int(x_pos), int(y_pos), anchor=tk.NW, image=sprite_image)

    root.mainloop()

create_sprites()
"""
    with open(py_file_path, "w") as f:
        f.write(data)
    logging.info("Scene script written to %s", py_file_path)

def convert_string_to_dict(input_str):
    input_str = input_str.strip()

    if input_str.startswith('{') and input_str.endswith('}'):
        input_str = input_str[1:-1]
    elif input_str.startswith('{'):
        input_str = input_str[1:]
    elif input_str.endswith('}'):
        input_str = input_str[:-1]

    pairs = []
    current_pair = ''
    in_quotes = False

    for char in input_str:
        if char == '"':
            in_quotes = not in_quotes
        if char == ',' and not in_quotes:
            pairs.append(current_pair.strip())
            current_pair = ''
        else:
            current_pair += char

    if current_pair.strip():
        pairs.append(current_pair.strip())

    result_dict = {}

    for pair in pairs:
        equals_index = pair.find('=')
        if equals_index != -1:
            key = pair[:equals_index].strip()
            value = pair[equals_index + 1:].strip()

            if value.startswith('"') and value.endswith('"'):
                value = value[1:-1]

            result_dict[key] = value

    return result_dict

def build_default_data(scene_name, py_file_path):
    data = f"""
import tkinter as tk

root = tk.Tk()
root.title("{scene_name}")
root.config(bg="#3e3e3e")
root.iconbitmap('assets/images/cogs.ico')
root.geometry("675x225")
root.minsize(width=675, height=225)
root.maxsize(width=675, height=225)
root.mainloop()
"""
    with open(py_file_path, "w") as f:
        f.write(data)
    logging.info("Default scene script written to %s", py_file_path)

def process_scene(scene_name, scene_sprites, scene_scripts, exe_file_path, py_file_path, parent_folder, scripting_language):
    try:
        if scene_sprites == "{}" and scene_scripts == "None":
            clean_previous_builds(parent_folder)
            build_default_data(scene_name, py_file_path)
        else:
            if scene_sprites != "{}":
                clean_previous_builds(parent_folder)
                build_project_with_data(scene_name, scene_sprites, scene_scripts, parent_folder, py_file_path)

        compile_and_run(py_file_path, exe_file_path, parent_folder, scripting_language)
        logging.info("Build succeeded.")
    except Exception as e:
        logging.error("Build failed: %s", e)
        raise

def clean_previous_builds(parent_folder):
    filter_extension = "*.exe"
    pattern = os.path.join(parent_folder, "bin", filter_extension)
    files_to_remove = glob.glob(pattern)
    for file_path in files_to_remove:
        delete_file(file_path)

    start_destin = "assets/images/cogs.ico"
    end_destin = os.path.join(parent_folder, "bin", "assets", "images", "cogs.ico")
    shutil.copyfile(start_destin, end_destin)
    logging.info("Copied %s to %s", start_destin, end_destin)

def compile_and_run(py_file_path, exe_file_path, parent_folder, scripting_language):
    start_time = perf_counter()
    compile_into_exe(py_file_path, os.path.join(parent_folder, "bin"))
    delete_file(py_file_path)
    delete_file(py_file_path[:-2] + "cmd")
    delete_folder(py_file_path[:-2] + "build")
    run_exe(exe_file_path)
    end_time = perf_counter()
    total_time = round(end_time - start_time, 2)
    logging.info("Build succeeded in %s seconds.", total_time)
    find_and_run_scripts(parent_folder, scripting_language)

def find_and_run_scripts(parent_folder, scripting_language):
    scripts_dir = os.path.join(parent_folder, "bin", "assets", "scripts")
    
    if not os.path.exists(scripts_dir):
        logging.warning("Scripts directory not found: %s", scripts_dir)
        return
    
    if scripting_language == "csharp":
        script_files = glob.glob(os.path.join(scripts_dir, "*.csx"))
        if not script_files:
            logging.warning("No scripts found in %s", scripts_dir)
            return
        
        for script_file in script_files:
            try:
                script_cmd = ["cmd", "/c", f'dotnet script {os.path.basename(script_file)}']
                logging.info("Running script: %s", subprocess.list2cmdline(script_cmd))
                subprocess.run(script_cmd, cwd=scripts_dir, check=True, shell=True)
                logging.info("Script '%s' executed successfully.", script_file)
            except subprocess.CalledProcessError as e:
                logging.error("Error running script '%s': %s", script_file, e)
            except Exception as e:
                logging.error("An error occurred while running script '%s': %s", script_file, e)
    elif scripting_language == "python":
        script_files = glob.glob(os.path.join(scripts_dir, "*.py"))
        if not script_files:
            logging.warning("No scripts found in %s", scripts_dir)
            return
        
        for script_file in script_files:
            try:
                script_cmd = ["python", script_file]
                logging.info("Running script: %s", subprocess.list2cmdline(script_cmd))
                subprocess.run(script_cmd, cwd=scripts_dir, check=True)
                logging.info("Script '%s' executed successfully.", script_file)
            except subprocess.CalledProcessError as e:
                logging.error("Error running script '%s': %s", script_file, e)
            except Exception as e:
                logging.error("An error occurred while running script '%s': %s", script_file, e)

if __name__ == "__main__":
    try:
        scene_name = sys.argv[1]
        scene_sprites = sys.argv[2]
        exe_file_path = sys.argv[3]
        py_file_path = sys.argv[4]
        parent_folder = sys.argv[5]
        manipulate_scene_name = sys.argv[6]
        scene_scripts = sys.argv[7]
        scripting_language = sys.argv[8]

        scene_sprites = json.loads(re.sub(r'(\w+):', r'"\1":', scene_sprites))

        log_path = os.path.join(parent_folder, "bin", "build.log")
        setup_logging(log_path)

        process_scene(scene_name, scene_sprites, scene_scripts, exe_file_path, py_file_path, parent_folder, scripting_language)
    
    except IndexError:
        logging.error("Not enough command line arguments provided.")
    except json.JSONDecodeError as e:
        logging.error("Error decoding JSON: %s", e)
    except Exception as e:
        logging.error("An unexpected error occurred: %s", e)
